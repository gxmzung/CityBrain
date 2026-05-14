import asyncio
from datetime import datetime, time
from typing import Optional

from fastapi import APIRouter, Query
from fastapi.responses import HTMLResponse

from app.routes.vision_history import save_current_vision_congestion

router = APIRouter(tags=["vision-auto-logging"])

_auto_task: Optional[asyncio.Task] = None
_auto_state = {
    "enabled": False,
    "interval_seconds": 60,
    "operating_start": "11:30",
    "operating_end": "13:30",
    "respect_operating_window": True,
    "success_count": 0,
    "skip_count": 0,
    "failure_count": 0,
    "last_saved_at": None,
    "last_skipped_at": None,
    "last_error": None,
    "started_at": None,
    "last_result": None,
}


def _parse_hhmm(value: str) -> time:
    try:
        hour, minute = value.strip().split(":")
        return time(hour=int(hour), minute=int(minute))
    except Exception:
        raise ValueError("time must be HH:MM format")


def _now_hhmm() -> str:
    return datetime.now().strftime("%H:%M")


def _is_within_operating_window(now: Optional[time] = None) -> bool:
    if not _auto_state["respect_operating_window"]:
        return True

    now_time = now or datetime.now().time()
    start = _parse_hhmm(_auto_state["operating_start"])
    end = _parse_hhmm(_auto_state["operating_end"])

    if start <= end:
        return start <= now_time <= end

    # Handles overnight windows, for example 22:00 ~ 02:00
    return now_time >= start or now_time <= end


async def _auto_logging_loop():
    while _auto_state["enabled"]:
        try:
            if _is_within_operating_window():
                result = await save_current_vision_congestion()
                _auto_state["success_count"] += 1
                _auto_state["last_saved_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                _auto_state["last_error"] = None
                _auto_state["last_result"] = result
            else:
                _auto_state["skip_count"] += 1
                _auto_state["last_skipped_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                _auto_state["last_result"] = {
                    "ok": True,
                    "message": "skipped because current time is outside operating window",
                    "now": _now_hhmm(),
                    "operating_start": _auto_state["operating_start"],
                    "operating_end": _auto_state["operating_end"],
                }
        except Exception as exc:
            _auto_state["failure_count"] += 1
            _auto_state["last_error"] = str(exc)

        await asyncio.sleep(int(_auto_state["interval_seconds"]))


def _task_running() -> bool:
    return _auto_task is not None and not _auto_task.done()


@router.get("/api/vision/auto-logging/status")
def get_auto_logging_status():
    within_window = _is_within_operating_window()

    return {
        "ok": True,
        "enabled": _auto_state["enabled"],
        "task_running": _task_running(),
        "interval_seconds": _auto_state["interval_seconds"],
        "operating_start": _auto_state["operating_start"],
        "operating_end": _auto_state["operating_end"],
        "respect_operating_window": _auto_state["respect_operating_window"],
        "within_operating_window": within_window,
        "now_hhmm": _now_hhmm(),
        "success_count": _auto_state["success_count"],
        "skip_count": _auto_state["skip_count"],
        "failure_count": _auto_state["failure_count"],
        "last_saved_at": _auto_state["last_saved_at"],
        "last_skipped_at": _auto_state["last_skipped_at"],
        "last_error": _auto_state["last_error"],
        "started_at": _auto_state["started_at"],
        "last_result": _auto_state["last_result"],
        "purpose": "Automatically save people-count-based congestion statistics during the configured operating window without storing original video.",
    }


@router.post("/api/vision/auto-logging/start")
async def start_auto_logging(
    interval_seconds: int = Query(default=60, ge=10, le=3600),
    start_time: str = Query(default="11:30"),
    end_time: str = Query(default="13:30"),
    respect_operating_window: bool = Query(default=True),
):
    global _auto_task

    _parse_hhmm(start_time)
    _parse_hhmm(end_time)

    _auto_state["enabled"] = True
    _auto_state["interval_seconds"] = interval_seconds
    _auto_state["operating_start"] = start_time
    _auto_state["operating_end"] = end_time
    _auto_state["respect_operating_window"] = respect_operating_window

    if not _auto_state["started_at"]:
        _auto_state["started_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    if not _task_running():
        _auto_task = asyncio.create_task(_auto_logging_loop())

    return {
        "ok": True,
        "message": "vision auto logging started",
        "enabled": _auto_state["enabled"],
        "interval_seconds": _auto_state["interval_seconds"],
        "operating_start": _auto_state["operating_start"],
        "operating_end": _auto_state["operating_end"],
        "respect_operating_window": _auto_state["respect_operating_window"],
        "within_operating_window": _is_within_operating_window(),
        "task_running": _task_running(),
    }


@router.post("/api/vision/auto-logging/stop")
def stop_auto_logging():
    _auto_state["enabled"] = False

    return {
        "ok": True,
        "message": "vision auto logging stopped",
        "enabled": _auto_state["enabled"],
        "task_running": _task_running(),
    }


@router.post("/api/vision/auto-logging/run-once")
async def run_auto_logging_once(
    ignore_operating_window: bool = Query(default=True)
):
    if not ignore_operating_window and not _is_within_operating_window():
        _auto_state["skip_count"] += 1
        _auto_state["last_skipped_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        return {
            "ok": True,
            "message": "skipped because current time is outside operating window",
            "within_operating_window": False,
            "now_hhmm": _now_hhmm(),
            "operating_start": _auto_state["operating_start"],
            "operating_end": _auto_state["operating_end"],
            "last_skipped_at": _auto_state["last_skipped_at"],
        }

    result = await save_current_vision_congestion()
    _auto_state["success_count"] += 1
    _auto_state["last_saved_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    _auto_state["last_error"] = None
    _auto_state["last_result"] = result

    return {
        "ok": True,
        "message": "vision congestion saved once",
        "result": result,
        "last_saved_at": _auto_state["last_saved_at"],
        "within_operating_window": _is_within_operating_window(),
    }


@router.get("/admin/vision-auto-logging", response_class=HTMLResponse)
def admin_vision_auto_logging_page():
    return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <title>CityBrain Vision Auto Logging</title>
  <style>
    body {
      margin: 0;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f6f7fb;
      color: #111827;
    }
    .wrap {
      max-width: 1080px;
      margin: 0 auto;
      padding: 32px 20px;
    }
    .card {
      background: white;
      border-radius: 20px;
      padding: 24px;
      box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
      margin-bottom: 18px;
    }
    h1 {
      margin: 0 0 8px;
      font-size: 28px;
    }
    h2 {
      margin: 0 0 14px;
      font-size: 20px;
    }
    .sub {
      color: #6b7280;
      margin-bottom: 24px;
      line-height: 1.6;
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 14px;
    }
    .metric {
      background: #f9fafb;
      border: 1px solid #e5e7eb;
      border-radius: 16px;
      padding: 16px;
    }
    .metric .label {
      font-size: 13px;
      color: #6b7280;
      margin-bottom: 8px;
    }
    .metric .value {
      font-size: 22px;
      font-weight: 800;
      word-break: break-all;
    }
    .row {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      align-items: center;
      margin-top: 18px;
    }
    button {
      border: 0;
      border-radius: 12px;
      padding: 12px 16px;
      font-weight: 700;
      cursor: pointer;
    }
    .primary {
      background: #111827;
      color: white;
    }
    .danger {
      background: #ef4444;
      color: white;
    }
    .plain {
      background: #e5e7eb;
      color: #111827;
    }
    input {
      width: 120px;
      padding: 11px 12px;
      border: 1px solid #d1d5db;
      border-radius: 12px;
      font-size: 14px;
    }
    label {
      font-size: 13px;
      color: #374151;
      font-weight: 700;
    }
    pre {
      background: #0f172a;
      color: #d1fae5;
      border-radius: 16px;
      padding: 18px;
      overflow-x: auto;
      font-size: 13px;
    }
    .links a {
      display: inline-block;
      margin-right: 12px;
      color: #2563eb;
      text-decoration: none;
      font-weight: 700;
    }
    .badge {
      display: inline-block;
      border-radius: 999px;
      padding: 6px 10px;
      background: #eef2ff;
      color: #3730a3;
      font-size: 13px;
      font-weight: 800;
    }
  </style>
</head>
<body>
  <div class="wrap">
    <div class="card">
      <h1>CityBrain Vision Auto Logging</h1>
      <div class="sub">
        YOLO 혼잡도 값을 설정한 운영 시간대에만 자동 저장하는 v9.5 관리자 화면입니다.
        영상 원본은 저장하지 않고 사람 수, 혼잡도, 측정 시각 중심의 통계값만 기록합니다.
      </div>

      <div class="grid">
        <div class="metric">
          <div class="label">Enabled</div>
          <div class="value" id="enabled">-</div>
        </div>
        <div class="metric">
          <div class="label">Task Running</div>
          <div class="value" id="running">-</div>
        </div>
        <div class="metric">
          <div class="label">Within Window</div>
          <div class="value" id="withinWindow">-</div>
        </div>
        <div class="metric">
          <div class="label">Operating Window</div>
          <div class="value" id="window">-</div>
        </div>
        <div class="metric">
          <div class="label">Interval</div>
          <div class="value" id="interval">-</div>
        </div>
        <div class="metric">
          <div class="label">Now</div>
          <div class="value" id="now">-</div>
        </div>
        <div class="metric">
          <div class="label">Success Count</div>
          <div class="value" id="success">-</div>
        </div>
        <div class="metric">
          <div class="label">Skip Count</div>
          <div class="value" id="skip">-</div>
        </div>
        <div class="metric">
          <div class="label">Failure Count</div>
          <div class="value" id="failure">-</div>
        </div>
        <div class="metric">
          <div class="label">Last Saved</div>
          <div class="value" id="lastSaved">-</div>
        </div>
        <div class="metric">
          <div class="label">Last Skipped</div>
          <div class="value" id="lastSkipped">-</div>
        </div>
        <div class="metric">
          <div class="label">Window Mode</div>
          <div class="value" id="windowMode">-</div>
        </div>
      </div>

      <div class="row">
        <label>Interval</label>
        <input id="intervalInput" type="number" min="10" max="3600" value="60" />

        <label>Start</label>
        <input id="startInput" type="time" value="11:30" />

        <label>End</label>
        <input id="endInput" type="time" value="13:30" />

        <label>
          <input id="respectWindowInput" type="checkbox" checked />
          운영 시간대 적용
        </label>
      </div>

      <div class="row">
        <button class="primary" onclick="startAuto()">Start Auto Logging</button>
        <button class="danger" onclick="stopAuto()">Stop</button>
        <button class="plain" onclick="runOnce()">Run Once</button>
        <button class="plain" onclick="runOnceWithWindow()">Run Once with Window Check</button>
        <button class="plain" onclick="loadStatus()">Refresh</button>
      </div>
    </div>

    <div class="card links">
      <span class="badge">v9.5 operating window</span>
      <br /><br />
      <a href="/admin/vision-history">Vision History</a>
      <a href="/admin/vision-report">Vision Report</a>
      <a href="/api/vision/history/export.csv?limit=1000">Download CSV</a>
    </div>

    <div class="card">
      <h2>Raw Status</h2>
      <pre id="raw">Loading...</pre>
    </div>
  </div>

  <script>
    async function loadStatus() {
      const res = await fetch("/api/vision/auto-logging/status");
      const data = await res.json();

      document.getElementById("enabled").textContent = data.enabled ? "ON" : "OFF";
      document.getElementById("running").textContent = data.task_running ? "YES" : "NO";
      document.getElementById("withinWindow").textContent = data.within_operating_window ? "YES" : "NO";
      document.getElementById("window").textContent = data.operating_start + " ~ " + data.operating_end;
      document.getElementById("interval").textContent = data.interval_seconds + "s";
      document.getElementById("now").textContent = data.now_hhmm || "-";
      document.getElementById("success").textContent = data.success_count;
      document.getElementById("skip").textContent = data.skip_count;
      document.getElementById("failure").textContent = data.failure_count;
      document.getElementById("lastSaved").textContent = data.last_saved_at || "-";
      document.getElementById("lastSkipped").textContent = data.last_skipped_at || "-";
      document.getElementById("windowMode").textContent = data.respect_operating_window ? "ON" : "OFF";
      document.getElementById("raw").textContent = JSON.stringify(data, null, 2);

      document.getElementById("intervalInput").value = data.interval_seconds || 60;
      document.getElementById("startInput").value = data.operating_start || "11:30";
      document.getElementById("endInput").value = data.operating_end || "13:30";
      document.getElementById("respectWindowInput").checked = !!data.respect_operating_window;
    }

    async function startAuto() {
      const interval = document.getElementById("intervalInput").value || 60;
      const start = document.getElementById("startInput").value || "11:30";
      const end = document.getElementById("endInput").value || "13:30";
      const respect = document.getElementById("respectWindowInput").checked;

      const url = "/api/vision/auto-logging/start"
        + "?interval_seconds=" + encodeURIComponent(interval)
        + "&start_time=" + encodeURIComponent(start)
        + "&end_time=" + encodeURIComponent(end)
        + "&respect_operating_window=" + encodeURIComponent(respect);

      await fetch(url, { method: "POST" });
      await loadStatus();
    }

    async function stopAuto() {
      await fetch("/api/vision/auto-logging/stop", {
        method: "POST"
      });
      await loadStatus();
    }

    async function runOnce() {
      await fetch("/api/vision/auto-logging/run-once?ignore_operating_window=true", {
        method: "POST"
      });
      await loadStatus();
    }

    async function runOnceWithWindow() {
      await fetch("/api/vision/auto-logging/run-once?ignore_operating_window=false", {
        method: "POST"
      });
      await loadStatus();
    }

    loadStatus();
    setInterval(loadStatus, 5000);
  </script>
</body>
</html>
"""
