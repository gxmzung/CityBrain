from fastapi import APIRouter
from fastapi.responses import HTMLResponse

router = APIRouter(tags=["student-vision-page"])


@router.get("/student/vision-status", response_class=HTMLResponse)
def student_vision_status_page():
    return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>CityBrain AI 혼잡도</title>
  <style>
    body {
      margin: 0;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f8fafc;
      color: #0f172a;
    }
    .wrap {
      max-width: 760px;
      margin: 0 auto;
      padding: 28px 18px;
    }
    .hero {
      background: linear-gradient(135deg, #0f766e, #0369a1);
      color: white;
      border-radius: 24px;
      padding: 28px;
      box-shadow: 0 18px 40px rgba(15, 23, 42, 0.18);
    }
    .hero h1 {
      margin: 0 0 8px;
      font-size: 28px;
      line-height: 1.25;
    }
    .hero p {
      margin: 0;
      opacity: 0.92;
      font-size: 15px;
    }
    .card {
      margin-top: 20px;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 22px;
      padding: 22px;
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
    }
    .status-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 14px;
      margin-top: 16px;
    }
    .metric {
      background: #f1f5f9;
      border-radius: 18px;
      padding: 18px;
    }
    .label {
      color: #64748b;
      font-size: 13px;
      margin-bottom: 8px;
    }
    .value {
      font-size: 28px;
      font-weight: 800;
      letter-spacing: -0.02em;
    }
    .badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-radius: 999px;
      font-size: 13px;
      font-weight: 700;
      background: #ecfeff;
      color: #0e7490;
      margin-top: 14px;
    }
    .note {
      margin-top: 16px;
      color: #64748b;
      font-size: 13px;
      line-height: 1.6;
    }
    .ok {
      color: #059669;
    }
    .warn {
      color: #d97706;
    }
    .error {
      color: #dc2626;
    }
    pre {
      margin-top: 16px;
      background: #0f172a;
      color: #e2e8f0;
      padding: 16px;
      border-radius: 16px;
      overflow: auto;
      font-size: 12px;
    }
    @media (max-width: 560px) {
      .status-row {
        grid-template-columns: 1fr;
      }
      .hero h1 {
        font-size: 23px;
      }
    }
  </style>
</head>
<body>
  <div class="wrap">
    <section class="hero">
      <h1>CityBrain AI 혼잡도 추정</h1>
      <p>YOLO 기반 사람 수 인식 결과를 바탕으로 학생식당 혼잡도를 표시합니다.</p>
    </section>

    <section class="card">
      <div id="connection" class="badge">연결 확인 중...</div>

      <div class="status-row">
        <div class="metric">
          <div class="label">현재 혼잡도</div>
          <div id="congestion" class="value">-</div>
        </div>
        <div class="metric">
          <div class="label">인식된 사람 수</div>
          <div id="personCount" class="value">-</div>
        </div>
      </div>

      <div class="status-row">
        <div class="metric">
          <div class="label">데이터 출처</div>
          <div id="source" class="value" style="font-size:18px;">-</div>
        </div>
        <div class="metric">
          <div class="label">업데이트 시각</div>
          <div id="updatedAt" class="value" style="font-size:18px;">-</div>
        </div>
      </div>

      <div class="note">
        이 화면은 개인 식별이나 영상 원본 저장이 아니라, 사람 수 기반 혼잡도 통계값 산출 가능성을 검증하기 위한 MVP 화면입니다.
      </div>

      <pre id="raw">loading...</pre>
    </section>
  </div>

  <script>
    async function loadVisionStatus() {
      const connection = document.getElementById("connection");
      const congestion = document.getElementById("congestion");
      const personCount = document.getElementById("personCount");
      const source = document.getElementById("source");
      const updatedAt = document.getElementById("updatedAt");
      const raw = document.getElementById("raw");

      try {
        const res = await fetch("/api/vision/congestion");
        const data = await res.json();

        raw.textContent = JSON.stringify(data, null, 2);

        if (data.ok === true) {
          connection.textContent = "AI 혼잡도 모듈 연결됨";
          connection.className = "badge ok";
        } else {
          connection.textContent = "수동 확인 필요";
          connection.className = "badge warn";
        }

        congestion.textContent = data.congestion ?? "-";
        personCount.textContent = data.person_count ?? "-";
        source.textContent = data.source ?? "-";
        updatedAt.textContent = data.updated_at ?? "-";
      } catch (err) {
        connection.textContent = "연결 실패";
        connection.className = "badge error";
        congestion.textContent = "수동 확인 필요";
        personCount.textContent = "-";
        source.textContent = "fallback";
        updatedAt.textContent = "-";
        raw.textContent = String(err);
      }
    }

    loadVisionStatus();
    setInterval(loadVisionStatus, 2000);
  </script>
</body>
</html>
"""
