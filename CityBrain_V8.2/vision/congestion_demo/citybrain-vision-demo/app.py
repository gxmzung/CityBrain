import os
import time
import threading
from typing import Dict, Any, Union

import cv2
import numpy as np
from fastapi import FastAPI
from fastapi.responses import HTMLResponse, StreamingResponse
from ultralytics import YOLO


# =========================
# Config
# =========================
VIDEO_SOURCE = os.getenv("VIDEO_SOURCE", "0")
YOLO_MODEL = os.getenv("YOLO_MODEL", "yolo11n.pt")

CONF_THRESHOLD = float(os.getenv("CONF_THRESHOLD", "0.35"))

# 혼잡도 기준값. 발표용 데모라면 숫자는 현장에 맞게 조정 가능.
NORMAL_LIMIT = int(os.getenv("NORMAL_LIMIT", "5"))
CROWDED_LIMIT = int(os.getenv("CROWDED_LIMIT", "10"))

FRAME_SKIP = int(os.getenv("FRAME_SKIP", "2"))


def parse_video_source(src: str) -> Union[int, str]:
    """
    VIDEO_SOURCE가 0, 1 같은 숫자면 웹캠 인덱스로 처리.
    아니면 RTSP URL 또는 영상 파일 경로로 처리.
    """
    if src.isdigit():
        return int(src)
    return src


def congestion_status(person_count: int) -> str:
    if person_count >= CROWDED_LIMIT:
        return "혼잡"
    if person_count >= NORMAL_LIMIT:
        return "보통"
    return "여유"


# =========================
# Global State
# =========================
app = FastAPI(title="CityBrain Vision Congestion Demo")

model = YOLO(YOLO_MODEL)

latest: Dict[str, Any] = {
    "person_count": 0,
    "congestion": "대기 중",
    "updated_at": None,
    "source": VIDEO_SOURCE,
}

latest_frame = None
frame_lock = threading.Lock()


# =========================
# Vision Loop
# =========================
def vision_loop():
    global latest_frame, latest

    source = parse_video_source(VIDEO_SOURCE)
    cap = cv2.VideoCapture(source)

    if not cap.isOpened():
        print(f"[ERROR] Cannot open video source: {VIDEO_SOURCE}")
        latest["congestion"] = "영상 연결 실패"
        return

    frame_index = 0

    while True:
        ok, frame = cap.read()

        if not ok:
            print("[WARN] Frame read failed. Reconnecting...")
            time.sleep(1)
            cap.release()
            cap = cv2.VideoCapture(source)
            continue

        frame_index += 1

        # 너무 느리면 일부 프레임은 건너뜀
        if frame_index % FRAME_SKIP != 0:
            continue

        # YOLO person detection
        results = model.predict(
            frame,
            conf=CONF_THRESHOLD,
            classes=[0],  # COCO class 0 = person
            verbose=False,
        )

        person_count = 0
        annotated = frame.copy()

        for result in results:
            boxes = result.boxes

            if boxes is None:
                continue

            for box in boxes:
                person_count += 1

                x1, y1, x2, y2 = box.xyxy[0].cpu().numpy().astype(int)
                conf = float(box.conf[0].cpu().numpy())

                cv2.rectangle(
                    annotated,
                    (x1, y1),
                    (x2, y2),
                    (0, 180, 255),
                    2,
                )
                cv2.putText(
                    annotated,
                    f"person {conf:.2f}",
                    (x1, max(y1 - 8, 20)),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.5,
                    (0, 180, 255),
                    2,
                )

        status = congestion_status(person_count)

        # 화면 상단 정보 표시
        cv2.rectangle(annotated, (0, 0), (520, 80), (20, 20, 20), -1)
        cv2.putText(
            annotated,
            f"CityBrain Vision Demo",
            (15, 30),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.8,
            (255, 255, 255),
            2,
        )
        cv2.putText(
            annotated,
            f"People: {person_count} | Congestion: {status}",
            (15, 62),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.75,
            (0, 255, 200),
            2,
        )

        with frame_lock:
            latest_frame = annotated
            latest = {
                "person_count": person_count,
                "congestion": status,
                "updated_at": time.strftime("%Y-%m-%d %H:%M:%S"),
                "source": VIDEO_SOURCE,
                "method": "RTSP/Webcam + YOLO person detection",
                "privacy_note": "원본 저장 없이 사람 수 기반 혼잡도 통계값 산출 목적",
            }


# =========================
# API
# =========================
@app.get("/api/congestion/latest")
def get_latest_congestion():
    return latest


@app.get("/", response_class=HTMLResponse)
def index():
    return """
    <!doctype html>
    <html lang="ko">
    <head>
      <meta charset="utf-8" />
      <title>CityBrain Vision Demo</title>
      <style>
        body {
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
          background: #0f172a;
          color: white;
          margin: 0;
          padding: 32px;
        }
        .wrap {
          max-width: 1100px;
          margin: 0 auto;
        }
        h1 {
          margin-bottom: 8px;
        }
        .subtitle {
          color: #94a3b8;
          margin-bottom: 24px;
        }
        .card {
          background: #111827;
          border: 1px solid #334155;
          border-radius: 18px;
          padding: 20px;
          margin-bottom: 20px;
        }
        img {
          width: 100%;
          border-radius: 14px;
          border: 1px solid #334155;
          background: black;
        }
        .badge {
          display: inline-block;
          padding: 8px 12px;
          background: #0f766e;
          border-radius: 999px;
          margin-top: 10px;
        }
        pre {
          background: #020617;
          padding: 16px;
          border-radius: 12px;
          overflow: auto;
        }
      </style>
    </head>
    <body>
      <div class="wrap">
        <h1>CityBrain Vision Demo</h1>
        <div class="subtitle">
          RTSP/Webcam + YOLO 기반 사람 수 인식 및 혼잡도 추정 MVP
        </div>

        <div class="card">
          <h2>실시간 인식 화면</h2>
          <img src="/video_feed" />
          <div class="badge">개인 식별이 아니라 사람 수 기반 혼잡도 통계값 산출 목적</div>
        </div>

        <div class="card">
          <h2>API</h2>
          <p><code>/api/congestion/latest</code> 에서 현재 혼잡도 JSON을 확인할 수 있습니다.</p>
          <pre id="api">loading...</pre>
        </div>
      </div>

      <script>
        async function loadStatus() {
          const res = await fetch('/api/congestion/latest');
          const data = await res.json();
          document.getElementById('api').textContent = JSON.stringify(data, null, 2);
        }
        loadStatus();
        setInterval(loadStatus, 2000);
      </script>
    </body>
    </html>
    """


def mjpeg_generator():
    while True:
        with frame_lock:
            frame = latest_frame.copy() if latest_frame is not None else None

        if frame is None:
            blank = np.zeros((480, 640, 3), dtype=np.uint8)
            cv2.putText(
                blank,
                "Waiting for video source...",
                (60, 240),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.8,
                (255, 255, 255),
                2,
            )
            frame = blank

        ok, buffer = cv2.imencode(".jpg", frame)

        if not ok:
            continue

        yield (
            b"--frame\r\n"
            b"Content-Type: image/jpeg\r\n\r\n"
            + buffer.tobytes()
            + b"\r\n"
        )

        time.sleep(0.05)


@app.get("/video_feed")
def video_feed():
    return StreamingResponse(
        mjpeg_generator(),
        media_type="multipart/x-mixed-replace; boundary=frame",
    )


# =========================
# Startup
# =========================
@app.on_event("startup")
def startup_event():
    thread = threading.Thread(target=vision_loop, daemon=True)
    thread.start()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app:app",
        host="127.0.0.1",
        port=8001,
        reload=False,
    )
