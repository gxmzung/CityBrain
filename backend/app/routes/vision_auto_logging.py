import asyncio
from datetime import datetime
from typing import Optional

from fastapi import APIRouter, Query
from fastapi.responses import HTMLResponse

from app.routes.vision_history import save_current_vision_congestion

router = APIRouter(tags=["vision-auto-logging"])

_auto_task: Optional[asyncio.Task] = None
_auto_state = {
    "enabled": False,
    "interval_seconds": 60,
    "success_count": 0,
    "failure_count": 0,
    "last_saved_at": None,
    "last_error": None,
    "started_at": None,
}


async def _auto_logging_loop():
    while _auto_state["enabled"]:
        try:
            result = await save_current_vision_congestion()
            _auto_state["success_count"] += 1
            _auto_state["last_saved_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            _auto_state["last_error"] = None
            _auto_state["last_result"] = result
        except Exception as exc:
            _auto_state["failure_count"] += 1
            _auto_state["last_error"] = str(exc)

        await asyncio.sleep(int(_auto_state["interval_seconds"]))


def _task_running() -> bool:
    return _auto_task is not None and not _auto_task.done()


@router.get("/api/vision/auto-logging/status")
def get_auto_logging_status():
    return {
        "ok": True,
        "enabled": _auto_state["enabled"],
        "task_running": _task_running(),
        "interval_seconds": _auto_state["interval_seconds"],
        "success_count": _auto_state["success_count"],
        "failure_count": _auto_state["failure_count"],
        "last_saved_at": _auto_state["last_saved_at"],
        "last_error": _auto_state["last_error"],
        "started_at": _auto_state["started_at"],
        "purpose": "Automatically save people-count-based congestion statistics without storing original video.",
    }


@router.post("/api/vision/auto-logging/start")
async def start_auto_logging(
    interval_seconds: int = Query(default=60, ge=10, le=3600)
):
    global _auto_task

    _auto_state["enabled"] = True
    _auto_state["interval_seconds"] = interval_seconds

    if not _auto_state["started_at"]:
        _auto_state["started_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    if not _task_running():
        _auto_task = asyncio.create_task(_auto_logging_loop())

    return {
        "ok": True,
        "message": "vision auto logging started",
        "enabled": _auto_state["enabled"],
        "interval_seconds": _auto_state["interval_seconds"],
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
async def run_auto_logging_once():
    result = await save_current_vision_congestion()
    _auto_state["success_count"] += 1
    _auto_state["last_saved_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    _auto_state["last_error"] = None

    return {
        "ok": True,
        "message": "vision congestion saved once",
        "result": result,
        "last_saved_at": _auto_state["last_saved_at"],
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
      max-width: 980px;
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
  </style>
</head>
<body>
  <div class="wrap">
    <div class="card">
      <h1>CityBrain Vision Auto Logging</h1>
      <div class="sub">
        YOLO 혼잡도 값을 일정 주기로 자동 저장하는 v9.4 관리자 화면입니다.
        영상 원본은 저장하지 않고 사람 수, 혼잡도, 측정 시각 중심의 통계값만 기록하는 것을 목표로 합니다.
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
          <div class="label">Interval</div>
          <div class="value" id="interval">-</div>
        </div>
        <div class="metric">
          <div class="label">Success Count</div>
          <div class="value" id="success">-</div>
        </div>
        <div class="metric">
          <div class="label">Failure Count</div>
          <div class="value" id="failure">-</div>
        </div>
        <div class="metric">
          <div class="label">Last Saved</div>
          <div class="value" id="lastSaved">-</div>
        </div>
      </div>

      <div class="row">
        <input id="intervalInput" type="number" min="10" max="3600" value="60" />
        <button class="primary" onclick="startAuto()">Start Auto Logging</button>
        <button class="danger" onclick="stopAuto()">Stop</button>
        <button class="plain" onclick="runOnce()">Run Once</button>
        <button class="plain" onclick="loadStatus()">Refresh</button>
      </div>
    </div>

    <div class="card links">
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
      document.getElementById("interval").textContent = data.interval_seconds + "s";
      document.getElementById("success").textContent = data.success_count;
      document.getElementById("failure").textContent = data.failure_count;
      document.getElementById("lastSaved").textContent = data.last_saved_at || "-";
      document.getElementById("raw").textContent = JSON.stringify(data, null, 2);
    }

    async function startAuto() {
      const interval = document.getElementById("intervalInput").value || 60;
      await fetch("/api/vision/auto-logging/start?interval_seconds=" + interval, {
        method: "POST"
      });
      await loadStatus();
    }

    async function stopAuto() {
      await fetch("/api/vision/auto-logging/stop", {
        method: "POST"
      });
      await loadStatus();
    }

    async function runOnce() {
      await fetch("/api/vision/auto-logging/run-once", {
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
