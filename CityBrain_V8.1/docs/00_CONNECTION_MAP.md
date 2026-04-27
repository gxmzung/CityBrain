# CityBrain 연결 구조

## 실행 관계

```text
관리자 웹/키오스크 API
        ↓ POST
FastAPI backend
        ↓ SQLite 저장
학생 웹 / Android 앱
        ↓ GET polling
학생 폰 화면 자동 반영
```

## 서버 주소 정책

- Android Emulator: `http://10.0.2.2:8000/`
- 실제 폰: PC와 같은 Wi-Fi에서 `http://PC_IP:8000/`
- Windows에서는 루트의 `RUN_DEMO_WINDOWS.ps1`을 실행하면 PC IPv4를 자동 탐지하고 `android/gradle.properties`에 `CITYBRAIN_BASE_URL`을 자동 기록한다.

## 핵심 API

| 영역 | Method | Path | 용도 |
|---|---:|---|---|
| 학생 | GET | `/api/student/state` | 잔여 수량/혼잡도/대기시간 조회 |
| 학생 | GET | `/api/student/survey` | 설문 요약 조회 |
| 학생 | GET | `/api/student/notices` | 공지 조회 |
| 학생 인증 | POST | `/api/student/auth/login` | 학번 기반 MVP 로그인 |
| 관리자 | POST | `/admin/quick-sale` | 판매량/혼잡도 빠른 반영 |
| 키오스크 | POST | `/api/kiosk/sales` | 판매 데이터 연동용 API |
| 확장 | POST | `/api/facility/reports` | 시설 신고 접수/1차 분류 |
| 운영 | GET | `/api/ops/status` | DB/보안/서비스 상태 진단 |

## 시연 계정

- 관리자: `admin / change-me-now`
- 운영자: `operator / operator123`
- 학생 앱 인증 API: `20260001 / demo1234`
