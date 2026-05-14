from collections import defaultdict
from datetime import datetime
from statistics import mean

from fastapi import APIRouter, Depends, Query
from fastapi.responses import HTMLResponse

from app.core.admin_auth import require_admin_key
from app.routes.vision_history import init_vision_history_db, get_conn, row_to_dict

router = APIRouter(tags=["vision-pilot-report"])


def _safe_avg(values):
    values = [v for v in values if v is not None]
    if not values:
        return None
    return round(mean(values), 2)


def _get_logs(limit: int = 1000):
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

    return [row_to_dict(row) for row in rows]


def _hour_from_saved_at(saved_at: str):
    try:
        return datetime.strptime(saved_at, "%Y-%m-%d %H:%M:%S").strftime("%H:00")
    except Exception:
        return "unknown"


def build_pilot_report(limit: int = 1000):
    logs = _get_logs(limit=limit)

    total_logs = len(logs)
    valid_people = [
        log.get("person_count")
        for log in logs
        if isinstance(log.get("person_count"), int)
    ]

    max_people = max(valid_people) if valid_people else None
    min_people = min(valid_people) if valid_people else None
    avg_people = _safe_avg(valid_people)

    congestion_counts = defaultdict(int)
    hourly_people = defaultdict(list)
    recent_logs = logs[:10]

    for log in logs:
        congestion = log.get("congestion") or "unknown"
        congestion_counts[congestion] += 1

        hour = _hour_from_saved_at(log.get("saved_at") or "")
        if isinstance(log.get("person_count"), int):
            hourly_people[hour].append(log.get("person_count"))

    hourly_summary = []
    for hour in sorted(hourly_people.keys()):
        hourly_summary.append(
            {
                "hour": hour,
                "count": len(hourly_people[hour]),
                "avg_people": _safe_avg(hourly_people[hour]),
                "max_people": max(hourly_people[hour]) if hourly_people[hour] else None,
            }
        )

    if total_logs == 0:
        summary_text = (
            "아직 수집된 혼잡도 기록이 없습니다. 파일럿 운영 시간 동안 자동 기록을 실행한 뒤 "
            "리포트를 다시 확인해야 합니다."
        )
    else:
        most_common_congestion = max(
            congestion_counts.items(),
            key=lambda item: item[1],
        )[0]

        summary_text = (
            f"이번 파일럿 데이터는 총 {total_logs}건의 혼잡도 기록을 기반으로 한다. "
            f"평균 감지 인원은 {avg_people}명이며, 최대 감지 인원은 {max_people}명이다. "
            f"가장 많이 관측된 혼잡도 상태는 '{most_common_congestion}'이다. "
            "이 결과는 영상 원본 저장 없이 사람 수, 혼잡도, 측정 시각 중심의 통계값만으로 "
            "학생식당 운영 검토에 활용할 수 있는지 확인하기 위한 기초 자료다."
        )

    return {
        "ok": True,
        "generated_at": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "limit": limit,
        "total_logs": total_logs,
        "avg_people": avg_people,
        "max_people": max_people,
        "min_people": min_people,
        "congestion_counts": dict(congestion_counts),
        "hourly_summary": hourly_summary,
        "recent_logs": recent_logs,
        "summary_text": summary_text,
        "privacy_note": "This report uses stored statistics only: person count, congestion level, and timestamp. Original video is not stored.",
    }


@router.get("/api/vision/pilot-report", dependencies=[Depends(require_admin_key)])
def get_vision_pilot_report(limit: int = Query(default=1000, ge=1, le=5000)):
    return build_pilot_report(limit=limit)


@router.get("/admin/vision-pilot-report", response_class=HTMLResponse, dependencies=[Depends(require_admin_key)])
def admin_vision_pilot_report_page():
    return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>CityBrain 파일럿 운영 리포트</title>
  <style>
    body {
      margin: 0;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f8fafc;
      color: #0f172a;
    }
    .wrap {
      max-width: 1120px;
      margin: 0 auto;
      padding: 32px 20px;
    }
    .hero {
      background: linear-gradient(135deg, #111827, #2563eb);
      color: white;
      border-radius: 28px;
      padding: 30px;
      box-shadow: 0 18px 45px rgba(15, 23, 42, 0.18);
      margin-bottom: 18px;
    }
    .hero h1 {
      margin: 0 0 8px;
      font-size: 30px;
      letter-spacing: -0.03em;
    }
    .hero p {
      margin: 0;
      color: #dbeafe;
      line-height: 1.6;
    }
    .card {
      background: white;
      border-radius: 22px;
      padding: 24px;
      box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
      margin-bottom: 18px;
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 14px;
    }
    .metric {
      background: #f9fafb;
      border: 1px solid #e5e7eb;
      border-radius: 18px;
      padding: 16px;
    }
    .label {
      font-size: 13px;
      color: #64748b;
      margin-bottom: 8px;
      font-weight: 700;
    }
    .value {
      font-size: 26px;
      font-weight: 900;
      letter-spacing: -0.03em;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      overflow: hidden;
      border-radius: 16px;
    }
    th, td {
      padding: 12px;
      border-bottom: 1px solid #e5e7eb;
      text-align: left;
      font-size: 14px;
    }
    th {
      background: #f1f5f9;
      color: #334155;
      font-size: 13px;
    }
    .summary {
      background: #f8fafc;
      border: 1px solid #e5e7eb;
      border-radius: 18px;
      padding: 18px;
      line-height: 1.8;
      font-size: 15px;
      white-space: pre-wrap;
    }
    .links a {
      display: inline-block;
      margin-right: 12px;
      margin-bottom: 8px;
      color: #2563eb;
      text-decoration: none;
      font-weight: 800;
    }
    button {
      border: 0;
      border-radius: 999px;
      padding: 11px 16px;
      font-weight: 800;
      cursor: pointer;
      background: #111827;
      color: white;
      margin-right: 8px;
    }
    pre {
      background: #0f172a;
      color: #d1fae5;
      border-radius: 16px;
      padding: 18px;
      overflow-x: auto;
      font-size: 13px;
    }
  </style>
</head>
<body>
  <div class="wrap">
    <section class="hero">
      <h1>CityBrain 파일럿 운영 리포트</h1>
      <p>
        학생식당 혼잡도 자동 기록 데이터를 바탕으로 파일럿 운영 결과를 요약합니다.
        영상 원본 없이 사람 수, 혼잡도, 측정 시각 중심의 통계값만 사용합니다.
      </p>
    </section>

    <section class="card">
      <div class="grid">
        <div class="metric">
          <div class="label">총 기록 수</div>
          <div class="value" id="totalLogs">-</div>
        </div>
        <div class="metric">
          <div class="label">평균 인원</div>
          <div class="value" id="avgPeople">-</div>
        </div>
        <div class="metric">
          <div class="label">최대 인원</div>
          <div class="value" id="maxPeople">-</div>
        </div>
        <div class="metric">
          <div class="label">최소 인원</div>
          <div class="value" id="minPeople">-</div>
        </div>
      </div>
    </section>

    <section class="card">
      <h2>학교 제출용 요약 문장</h2>
      <div class="summary" id="summaryText">Loading...</div>
    </section>

    <section class="card">
      <h2>혼잡도별 기록 수</h2>
      <table>
        <thead>
          <tr>
            <th>혼잡도</th>
            <th>기록 수</th>
          </tr>
        </thead>
        <tbody id="congestionBody"></tbody>
      </table>
    </section>

    <section class="card">
      <h2>시간대별 평균 인원</h2>
      <table>
        <thead>
          <tr>
            <th>시간대</th>
            <th>기록 수</th>
            <th>평균 인원</th>
            <th>최대 인원</th>
          </tr>
        </thead>
        <tbody id="hourlyBody"></tbody>
      </table>
    </section>

    <section class="card">
      <h2>최근 기록 10개</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>저장 시각</th>
            <th>인원</th>
            <th>혼잡도</th>
            <th>소스</th>
          </tr>
        </thead>
        <tbody id="recentBody"></tbody>
      </table>
    </section>

    <section class="card links">
      <button onclick="loadReport()">새로고침</button>
      <a id="historyLink" href="#">기록 화면</a>
      <a id="csvLink" href="#">CSV 다운로드</a>
      <a id="autoLink" href="#">자동 기록 설정</a>
    </section>

    <section class="card">
      <h2>Raw Report JSON</h2>
      <pre id="raw">Loading...</pre>
    </section>
  </div>

  <script>
    const adminKey = new URLSearchParams(window.location.search).get("admin_key") || "";

    function withAdminKey(url) {
      const sep = url.includes("?") ? "&" : "?";
      return url + sep + "admin_key=" + encodeURIComponent(adminKey);
    }

    function td(value) {
      return "<td>" + (value === null || value === undefined ? "-" : value) + "</td>";
    }

    async function loadReport() {
      const res = await fetch(withAdminKey("/api/vision/pilot-report?limit=1000"));
      const data = await res.json();

      document.getElementById("totalLogs").textContent = data.total_logs ?? "-";
      document.getElementById("avgPeople").textContent = data.avg_people ?? "-";
      document.getElementById("maxPeople").textContent = data.max_people ?? "-";
      document.getElementById("minPeople").textContent = data.min_people ?? "-";
      document.getElementById("summaryText").textContent = data.summary_text || "-";
      document.getElementById("raw").textContent = JSON.stringify(data, null, 2);

      const congestionBody = document.getElementById("congestionBody");
      congestionBody.innerHTML = "";
      Object.entries(data.congestion_counts || {}).forEach(([name, count]) => {
        congestionBody.innerHTML += "<tr>" + td(name) + td(count) + "</tr>";
      });

      const hourlyBody = document.getElementById("hourlyBody");
      hourlyBody.innerHTML = "";
      (data.hourly_summary || []).forEach(row => {
        hourlyBody.innerHTML += "<tr>"
          + td(row.hour)
          + td(row.count)
          + td(row.avg_people)
          + td(row.max_people)
          + "</tr>";
      });

      const recentBody = document.getElementById("recentBody");
      recentBody.innerHTML = "";
      (data.recent_logs || []).forEach(row => {
        recentBody.innerHTML += "<tr>"
          + td(row.id)
          + td(row.saved_at)
          + td(row.person_count)
          + td(row.congestion)
          + td(row.source)
          + "</tr>";
      });

      document.getElementById("historyLink").href = withAdminKey("/admin/vision-history");
      document.getElementById("csvLink").href = withAdminKey("/api/vision/history/export.csv?limit=1000");
      document.getElementById("autoLink").href = withAdminKey("/admin/vision-auto-logging");
    }

    loadReport();
  </script>
</body>
</html>
"""
