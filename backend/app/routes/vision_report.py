from fastapi import APIRouter, Depends
from fastapi.responses import HTMLResponse
from app.core.admin_auth import require_admin_key

router = APIRouter(tags=["admin-vision-report"])


@router.get("/admin/vision-report", response_class=HTMLResponse, dependencies=[Depends(require_admin_key)])
def admin_vision_report_page():
    return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>CityBrain AI 혼잡도 운영 리포트</title>
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
      background: linear-gradient(135deg, #0f172a, #0f766e);
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
    .grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      margin-top: 22px;
    }
    .card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 22px;
      padding: 20px;
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.07);
    }
    .label {
      color: #64748b;
      font-size: 13px;
      margin-bottom: 8px;
    }
    .value {
      font-size: 28px;
      font-weight: 850;
      letter-spacing: -0.03em;
    }
    .value.small {
      font-size: 17px;
      line-height: 1.45;
    }
    .wide {
      grid-column: span 2;
    }
    .full {
      grid-column: 1 / -1;
    }
    .badge {
      display: inline-flex;
      align-items: center;
      padding: 8px 12px;
      border-radius: 999px;
      font-size: 13px;
      font-weight: 800;
      background: #ecfeff;
      color: #0e7490;
      margin-top: 14px;
    }
    .ok {
      color: #059669;
      background: #ecfdf5;
    }
    .warn {
      color: #d97706;
      background: #fffbeb;
    }
    .danger {
      color: #dc2626;
      background: #fef2f2;
    }
    .section-title {
      margin: 28px 0 12px;
      font-size: 20px;
      font-weight: 850;
      letter-spacing: -0.02em;
    }
    .decision {
      font-size: 18px;
      font-weight: 800;
      line-height: 1.55;
    }
    .desc {
      color: #475569;
      line-height: 1.7;
      font-size: 14px;
      margin-top: 10px;
    }
    pre {
      margin: 0;
      background: #0f172a;
      color: #e2e8f0;
      padding: 18px;
      border-radius: 18px;
      overflow: auto;
      font-size: 12px;
      line-height: 1.55;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 8px;
      font-size: 14px;
    }
    th, td {
      text-align: left;
      padding: 13px 10px;
      border-bottom: 1px solid #e2e8f0;
    }
    th {
      color: #475569;
      font-size: 13px;
      background: #f8fafc;
    }
    @media (max-width: 860px) {
      .grid {
        grid-template-columns: 1fr 1fr;
      }
      .wide {
        grid-column: span 2;
      }
    }
    @media (max-width: 560px) {
      .grid {
        grid-template-columns: 1fr;
      }
      .wide, .full {
        grid-column: span 1;
      }
      .hero h1 {
        font-size: 24px;
      }
    }
  </style>
</head>
<body>
  <div class="wrap">
    <section class="hero">
      <h1>CityBrain AI 혼잡도 운영 리포트</h1>
      <p>
        YOLO 기반 사람 수 인식 결과를 운영자가 확인할 수 있도록 정리한 리포트 화면입니다.
        학생 안내뿐 아니라 학교와 운영사의 의사결정 근거로 활용하는 것을 목표로 합니다.
      </p>
      <div id="connection" class="badge">연결 확인 중...</div>
    </section>

    <div class="grid">
      <div class="card">
        <div class="label">현재 혼잡도</div>
        <div id="congestion" class="value">-</div>
      </div>

      <div class="card">
        <div class="label">인식된 사람 수</div>
        <div id="personCount" class="value">-</div>
      </div>

      <div class="card">
        <div class="label">데이터 출처</div>
        <div id="source" class="value small">-</div>
      </div>

      <div class="card">
        <div class="label">업데이트 시각</div>
        <div id="updatedAt" class="value small">-</div>
      </div>

      <div class="card wide">
        <div class="label">운영 판단 메시지</div>
        <div id="decision" class="decision">-</div>
        <div class="desc">
          이 메시지는 현재 혼잡도와 인식된 사람 수를 바탕으로 운영자가 참고할 수 있도록 만든 MVP 수준의 판단 문구입니다.
        </div>
      </div>

      <div class="card wide">
        <div class="label">파일럿 운영 관점</div>
        <div class="decision">학생 안내 + 운영 리포트 동시 검증</div>
        <div class="desc">
          이 화면은 정식 운영 시스템이 아니라, 1주일 파일럿에서 혼잡도 데이터가 운영 개선에 도움이 되는지 확인하기 위한 관리자용 화면입니다.
        </div>
      </div>

      <div class="card full">
        <div class="section-title">운영 활용 예시</div>
        <table>
          <thead>
            <tr>
              <th>데이터</th>
              <th>운영 활용</th>
              <th>학교/운영사 관점 가치</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>사람 수</td>
              <td>현재 대기열·식당 체류 인원 추정</td>
              <td>혼잡 시간대 파악</td>
            </tr>
            <tr>
              <td>혼잡도</td>
              <td>학생에게 방문 시점 안내</td>
              <td>쏠림 완화 및 민원 감소</td>
            </tr>
            <tr>
              <td>업데이트 시각</td>
              <td>데이터 최신성 확인</td>
              <td>운영 신뢰도 판단</td>
            </tr>
            <tr>
              <td>연결 상태</td>
              <td>YOLO 모듈 장애 여부 확인</td>
              <td>수동 모드 전환 판단</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card full">
        <div class="section-title">Raw API Response</div>
        <pre id="raw">loading...</pre>
      </div>
    </div>
  </div>

  <script>
    function buildDecision(data) {
      if (data.ok !== true) {
        return "AI 혼잡도 모듈 연결이 불안정합니다. 현재는 수동 확인 또는 현장 확인이 필요합니다.";
      }

      const count = Number(data.person_count ?? 0);
      const congestion = data.congestion ?? "알 수 없음";

      if (congestion.includes("혼잡") || count >= 10) {
        return "현재 혼잡도가 높습니다. 학생 안내 화면에는 대기 가능성을 명확히 표시하고, 운영자는 줄 분산 또는 안내 문구를 검토할 수 있습니다.";
      }

      if (congestion.includes("보통") || count >= 5) {
        return "현재 혼잡도는 보통 수준입니다. 점심 피크 시간대라면 추가 상승 가능성을 관찰할 필요가 있습니다.";
      }

      return "현재 혼잡도는 낮은 편입니다. 학생에게 방문 가능 시간으로 안내하기 적합한 상태입니다.";
    }

    async function loadReport() {
      const connection = document.getElementById("connection");
      const congestion = document.getElementById("congestion");
      const personCount = document.getElementById("personCount");
      const source = document.getElementById("source");
      const updatedAt = document.getElementById("updatedAt");
      const decision = document.getElementById("decision");
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
        decision.textContent = buildDecision(data);
      } catch (err) {
        connection.textContent = "연결 실패";
        connection.className = "badge danger";
        congestion.textContent = "수동 확인 필요";
        personCount.textContent = "-";
        source.textContent = "fallback";
        updatedAt.textContent = "-";
        decision.textContent = "CityBrain 백엔드 또는 AI 혼잡도 API 연결을 확인해야 합니다.";
        raw.textContent = String(err);
      }
    }

    loadReport();
    setInterval(loadReport, 2000);
  </script>
</body>
</html>
"""
