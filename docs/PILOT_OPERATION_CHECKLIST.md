# CityBrain Pilot Operation Checklist

## 1. Purpose

This checklist defines what must be reviewed before running a CityBrain cafeteria congestion pilot in a real campus environment.

CityBrain should be operated as a people-count-based congestion statistics system, not as a surveillance or personal identification system.

---

## 2. Pre-Pilot Approval Checklist

| Check Item | Status |
|---|---|
| Pilot purpose is clearly defined | Not checked |
| School department or professor approval is obtained | Not checked |
| Cafeteria operator is informed | Not checked |
| Camera location is reviewed | Not checked |
| Privacy notice draft is prepared | Not checked |
| Admin access method is prepared | Not checked |
| Original video storage is disabled | Not checked |
| CSV export access is protected | Not checked |
| Failure handling process is prepared | Not checked |

---

## 3. Privacy Checklist

| Check Item | Required Direction |
|---|---|
| Original video storage | Disabled |
| Image capture storage | Disabled |
| Face recognition | Not used |
| Student identification | Not used |
| Individual movement tracking | Not used |
| Stored data | Person count, congestion level, timestamp |
| Camera range | Entrance or queue area only |
| Notice signage | Required before real pilot |
| Admin page access | Admin key required |

---

## 4. Technical Checklist

| Check Item | Test Method |
|---|---|
| YOLO module runs | Open http://127.0.0.1:8081/api/congestion/latest |
| Backend runs | Open http://127.0.0.1:8080 |
| Student page opens | Open /student/vision-status |
| Admin report requires key | Open /admin/vision-report without and with admin_key |
| History page requires key | Open /admin/vision-history without and with admin_key |
| Auto logging page requires key | Open /admin/vision-auto-logging without and with admin_key |
| Pilot report requires key | Open /admin/vision-pilot-report without and with admin_key |
| CSV export requires key | Open /api/vision/history/export.csv without and with admin_key |
| Auto logging works | Start auto logging and check history records |
| Operating window works | Test inside and outside configured time window |

---

## 5. Recommended Pilot Operation Flow

1. Start YOLO vision module.
2. Start CityBrain backend.
3. Confirm student congestion page.
4. Confirm admin key protection.
5. Start auto logging during selected operation time.
6. Check vision history page.
7. Export CSV after pilot session.
8. Review pilot report page.
9. Compare selected records with manual observation.
10. Summarize whether the system is suitable for a longer pilot.

---

## 6. Run Commands

YOLO vision module:

    cd ~/Documents/001.개발/CityBrain/vision/congestion_demo
    source .venv/bin/activate
    python3 app.py

CityBrain backend:

    cd ~/Documents/001.개발/CityBrain/backend
    source ../.venv/bin/activate
    python3 -m uvicorn app.main:app --host 127.0.0.1 --port 8080 --reload

---

## 7. Key URLs

Student page:

    http://127.0.0.1:8080/student/vision-status

Admin pages:

    http://127.0.0.1:8080/admin/vision-report?admin_key=citybrain-local-admin
    http://127.0.0.1:8080/admin/vision-history?admin_key=citybrain-local-admin
    http://127.0.0.1:8080/admin/vision-auto-logging?admin_key=citybrain-local-admin
    http://127.0.0.1:8080/admin/vision-pilot-report?admin_key=citybrain-local-admin

APIs:

    http://127.0.0.1:8080/api/vision/congestion
    http://127.0.0.1:8080/api/vision/history?admin_key=citybrain-local-admin
    http://127.0.0.1:8080/api/vision/history/export.csv?admin_key=citybrain-local-admin
    http://127.0.0.1:8080/api/vision/pilot-report?admin_key=citybrain-local-admin

---

## 8. Pilot Notice Draft

    CityBrain 학생식당 혼잡도 안내 파일럿 운영 안내

    본 구역에서는 학생식당 혼잡도 안내 및 운영 개선 가능성 검토를 위해
    AI 기반 사람 수 통계 측정 파일럿이 진행될 수 있습니다.

    - 설치 목적: 학생식당 혼잡도 안내 및 운영 개선 검토
    - 처리 방식: 카메라 영상에서 사람 수 통계값만 산출
    - 저장 데이터: 사람 수, 혼잡도, 측정 시각
    - 저장하지 않는 데이터: 영상 원본, 얼굴 이미지, 개인 식별 정보
    - 촬영 범위: 학생식당 입구 또는 대기열 일부
    - 운영 시간: 파일럿 기간 중 지정 시간대
    - 관리 부서/담당자: 학교 협의 후 지정
    - 문의: 학교 협의 후 지정

---

## 9. Go / No-Go Decision

| Result | Decision |
|---|---|
| Data collection works and privacy conditions are satisfied | Continue pilot |
| Data collection works but accuracy is unstable | Continue with manual comparison |
| Admin access is not protected | Stop and fix before pilot |
| Original video is stored unintentionally | Stop immediately |
| Camera angle captures unnecessary areas | Reposition camera |
| Operator finds report useful | Consider longer pilot |
| Student page is rarely used | Improve UI or notification method |

---

## 10. Conclusion

CityBrain should only proceed to a real campus pilot after privacy, technical, and operational checks are completed.

A safe pilot is more important than a flashy demo.
