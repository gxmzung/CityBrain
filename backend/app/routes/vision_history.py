from datetime import datetime
from pathlib import Path
import sqlite3

from fastapi import APIRouter, Depends, Query
from app.core.admin_auth import require_admin_key
from fastapi.responses import HTMLResponse, Response

from app.routes.vision import get_vision_congestion

router = APIRouter(tags=["vision-history"])

DB_PATH = Path(__file__).resolve().parents[2] / "vision_history.db"


def get_conn():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_vision_history_db():
    with get_conn() as conn:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS vision_congestion_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                saved_at TEXT NOT NULL,
                ok INTEGER NOT NULL,
                source TEXT,
                person_count INTEGER,
                congestion TEXT,
                updated_at TEXT,
                method TEXT,
                privacy_note TEXT,
                raw_error TEXT
            )
            """
        )
        conn.commit()


def row_to_dict(row):
    return {
        "id": row["id"],
        "saved_at": row["saved_at"],
        "ok": bool(row["ok"]),
        "source": row["source"],
        "person_count": row["person_count"],
        "congestion": row["congestion"],
        "updated_at": row["updated_at"],
        "method": row["method"],
        "privacy_note": row["privacy_note"],
        "raw_error": row["raw_error"],
    }


@router.get("/api/vision/history/save")
async def save_current_vision_congestion():
    init_vision_history_db()

    data = await get_vision_congestion()
    saved_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    with get_conn() as conn:
        cur = conn.execute(
            """
            INSERT INTO vision_congestion_logs (
                saved_at,
                ok,
                source,
                person_count,
                congestion,
                updated_at,
                method,
                privacy_note,
                raw_error
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            (
                saved_at,
                1 if data.get("ok") else 0,
                data.get("source"),
                data.get("person_count"),
                data.get("congestion"),
                data.get("updated_at"),
                data.get("method"),
                data.get("privacy_note"),
                data.get("error") or data.get("message"),
            ),
        )
        conn.commit()
        log_id = cur.lastrowid

    return {
        "ok": True,
        "message": "vision congestion log saved",
        "id": log_id,
        "saved_at": saved_at,
        "data": data,
    }


@router.get("/api/vision/history", dependencies=[Depends(require_admin_key)])
def get_vision_history(limit: int = Query(default=20, ge=1, le=100)):
    init_vision_history_db()

    with get_conn() as conn:
        rows = conn.execute(
            """
            SELECT *
            FROM vision_congestion_logs
            ORDER BY id DESC
            LIMIT ?
            """,
            (limit,),
        ).fetchall()

    return {
        "ok": True,
        "count": len(rows),
        "logs": [row_to_dict(row) for row in rows],
    }


@router.get("/api/vision/history/summary", dependencies=[Depends(require_admin_key)])
def get_vision_history_summary():
    init_vision_history_db()

    with get_conn() as conn:
        total = conn.execute("SELECT COUNT(*) AS c FROM vision_congestion_logs").fetchone()["c"]
        latest = conn.execute(
            """
            SELECT *
            FROM vision_congestion_logs
            ORDER BY id DESC
            LIMIT 1
            """
        ).fetchone()

        by_congestion = conn.execute(
            """
            SELECT congestion, COUNT(*) AS count
            FROM vision_congestion_logs
            GROUP BY congestion
            ORDER BY count DESC
            """
        ).fetchall()

        avg_people = conn.execute(
            """
            SELECT AVG(person_count) AS avg_people
            FROM vision_congestion_logs
            WHERE person_count IS NOT NULL
            """
        ).fetchone()["avg_people"]

    return {
        "ok": True,
        "total_logs": total,
        "latest": row_to_dict(latest) if latest else None,
        "avg_people": round(avg_people, 2) if avg_people is not None else None,
        "by_congestion": [
            {"congestion": row["congestion"], "count": row["count"]}
            for row in by_congestion
        ],
    }


@router.get("/admin/vision-history", response_class=HTMLResponse, dependencies=[Depends(require_admin_key)])
def admin_vision_history_page():
    return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>CityBrain AI 혼잡도 기록</title>
  <style>
    body {
      margin: 0;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f8fafc;
      color: #0f172a;
    }
    .wrap {
      max-width: 1080px;
      margin: 0 auto;
      padding: 32px 20px;
    }
    .hero {
      background: linear-gradient(135deg, #111827, #2563eb);
      color: white;
      border-radius: 26px;
      padding: 30px;
      box-shadow: 0 18px 45px rgba(15, 23, 42, 0.18);
    }
    .hero h1 {
      margin: 0 0 8px;
      font-size: 30px;
      letter-spacing: -0.03em;
    }
    .hero p {
      margin: 0;
      color: #dbeafe;
      line-height: 1.55;
    }
    .actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      margin-top: 18px;
    }
    button {
      border: 0;
      border-radius: 999px;
      padding: 11px 16px;
      font-weight: 800;
      cursor: pointer;
      background: white;
      color: #1e3a8a;
    }
    .card {
      margin-top: 18px;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 22px;
      padding: 20px;
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.07);
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 14px;
      margin-top: 18px;
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
      font-size: 26px;
      font-weight: 850;
      letter-spacing: -0.03em;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 8px;
      font-size: 14px;
    }
    th, td {
      text-align: left;
      padding: 12px 10px;
      border-bottom: 1px solid #e2e8f0;
    }
    th {
      color: #475569;
      font-size: 13px;
      background: #f8fafc;
    }
    .note {
      color: #64748b;
      font-size: 13px;
      line-height: 1.6;
      margin-top: 12px;
    }
    .ok {
      color: #059669;
      font-weight: 800;
    }
    .fail {
      color: #dc2626;
      font-weight: 800;
    }
    @media (max-width: 760px) {
      .grid {
        grid-template-columns: 1fr 1fr;
      }
    }
    @media (max-width: 520px) {
      .grid {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <div class="wrap">
    <section class="hero">
      <h1>CityBrain AI 혼잡도 기록</h1>
      <p>
        YOLO 기반 혼잡도 값을 SQLite에 저장하고, 최근 기록을 운영자가 확인하는 화면입니다.
        학교 파일럿에서는 시간대별 혼잡도 변화와 운영 개선 근거를 확인하는 데 사용할 수 있습니다.
      </p>
      <div class="actions">
        <button onclick="saveNow()">현재 혼잡도 저장</button>
        <button onclick="loadAll()">기록 새로고침</button>
        <button onclick="toggleAutoSave()">자동 저장 ON/OFF</button>
        <button onclick="downloadCsv()">CSV 다운로드</button>
      </div>
      <p id="status" class="note">대기 중...</p>
    </section>

    <section class="grid">
      <div class="metric">
        <div class="label">총 기록 수</div>
        <div id="totalLogs" class="value">-</div>
      </div>
      <div class="metric">
        <div class="label">평균 인식 인원</div>
        <div id="avgPeople" class="value">-</div>
      </div>
      <div class="metric">
        <div class="label">최근 혼잡도</div>
        <div id="latestCongestion" class="value">-</div>
      </div>
      <div class="metric">
        <div class="label">자동 저장</div>
        <div id="autoSaveState" class="value">OFF</div>
      </div>
    </section>

    <section class="card">
      <h2>최근 혼잡도 기록</h2>
      <div class="note">
        현재는 MVP 단계이므로 브라우저에서 저장 요청을 눌러 기록을 누적합니다.
        이후에는 서버 스케줄러 또는 운영 시간대 자동 수집으로 확장할 수 있습니다.
      </div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>저장 시각</th>
            <th>상태</th>
            <th>혼잡도</th>
            <th>사람 수</th>
            <th>데이터 출처</th>
            <th>원본 업데이트</th>
          </tr>
        </thead>
        <tbody id="historyBody">
          <tr><td colspan="7">loading...</td></tr>
        </tbody>
      </table>
    </section>
  </div>

  <script>
      const adminKey = new URLSearchParams(window.location.search).get("admin_key") || "";
      function withAdminKey(url) {
        const sep = url.includes("?") ? "&" : "?";
        return url + sep + "admin_key=" + encodeURIComponent(adminKey);
      }

    let autoSave = false;
    let autoTimer = null;

    async function saveNow() {
      const status = document.getElementById("status");
      try {
        const res = await fetch("/api/vision/history/save");
        const data = await res.json();
        status.textContent = `저장 완료: #${data.id} / ${data.saved_at}`;
        await loadAll();
      } catch (err) {
        status.textContent = "저장 실패: " + String(err);
      }
    }

    async function loadAll() {
      await loadSummary();
      await loadHistory();
    }

    async function loadSummary() {
      const res = await fetch(withAdminKey("/api/vision/history/summary"));
      const data = await res.json();

      document.getElementById("totalLogs").textContent = data.total_logs ?? "-";
      document.getElementById("avgPeople").textContent = data.avg_people ?? "-";
      document.getElementById("latestCongestion").textContent =
        data.latest?.congestion ?? "-";
    }

    async function loadHistory() {
      const res = await fetch(withAdminKey("/api/vision/history?limit=30"));
      const data = await res.json();

      const body = document.getElementById("historyBody");

      if (!data.logs || data.logs.length === 0) {
        body.innerHTML = '<tr><td colspan="7">아직 저장된 기록이 없습니다.</td></tr>';
        return;
      }

      body.innerHTML = data.logs.map(log => `
        <tr>
          <td>${log.id}</td>
          <td>${log.saved_at ?? "-"}</td>
          <td class="${log.ok ? "ok" : "fail"}">${log.ok ? "정상" : "fallback"}</td>
          <td>${log.congestion ?? "-"}</td>
          <td>${log.person_count ?? "-"}</td>
          <td>${log.source ?? "-"}</td>
          <td>${log.updated_at ?? "-"}</td>
        </tr>
      `).join("");
    }

    function downloadCsv() {
      window.location.href = withAdminKey("/api/vision/history/export.csv?limit=1000");
    }

    function toggleAutoSave() {
      autoSave = !autoSave;
      const state = document.getElementById("autoSaveState");

      if (autoSave) {
        state.textContent = "ON";
        saveNow();
        autoTimer = setInterval(saveNow, 10000);
      } else {
        state.textContent = "OFF";
        if (autoTimer) {
          clearInterval(autoTimer);
          autoTimer = null;
        }
      }
    }

    loadAll();
    setInterval(loadAll, 5000);
  </script>
</body>
</html>
"""


@router.get("/api/vision/history/export.csv", dependencies=[Depends(require_admin_key)])
def export_vision_history_csv(limit: int = Query(default=100, ge=1, le=1000)):
    init_vision_history_db()

    with get_conn() as conn:
        rows = conn.execute(
            """
            SELECT *
            FROM vision_congestion_logs
            ORDER BY id DESC
            LIMIT ?
            """,
            (limit,),
        ).fetchall()

    headers = [
        "id",
        "saved_at",
        "ok",
        "source",
        "person_count",
        "congestion",
        "updated_at",
        "method",
        "privacy_note",
        "raw_error",
    ]

    lines = [",".join(headers)]

    for row in rows:
        values = []
        for key in headers:
            value = row[key]
            if value is None:
                value = ""
            value = str(value).replace('"', '""')
            values.append(f'"{value}"')
        lines.append(",".join(values))

    csv_text = "\n".join(lines)

    return Response(
        content=csv_text,
        media_type="text/csv; charset=utf-8",
        headers={
            "Content-Disposition": "attachment; filename=citybrain_vision_history.csv"
        },
    )

