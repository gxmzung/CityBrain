# CityBrain Final MVP

배재대학교 학생식당 실증 데모용 **관리자 웹 + 학생 모바일 웹앱 + 키오스크 연동 API** 패키지입니다.

## 이번 버전의 목표

- 관리자가 웹에서 잔여 수량, 혼잡도, 예상 대기시간을 수정
- 학생 화면은 휴대폰에서 앱처럼 보이고 2초마다 자동 갱신
- 키오스크 연동이 승인되면 `POST /api/kiosk/sales`로 판매 데이터를 넣어 동일하게 반영
- Android 앱에서 호출 가능한 `/api/student/state`, `/api/student/survey`, `/api/student/notices` 제공

## 실행

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## 접속

- 학생 모바일 화면: http://127.0.0.1:8000/
- 관리자 로그인: http://127.0.0.1:8000/admin/login
- API 문서: http://127.0.0.1:8000/docs

## 테스트 계정

- admin / change-me-now
- operator / operator123
- viewer / viewer123

## 시연 흐름

1. 학생 화면에서 `63개 남음 / 보통 / 7분` 확인
2. 관리자 로그인 후 `-5 판매 반영`, 혼잡도 `혼잡`, 예상 대기시간 `15분` 저장
3. 학생 휴대폰 화면이 자동으로 `58개 남음 / 혼잡 / 15분`으로 바뀌는 장면을 보여준다

## 키오스크 API 예시

```bash
curl -X POST http://127.0.0.1:8000/api/kiosk/sales \
  -H "Content-Type: application/json" \
  -d '{"menu_name":"돈까스덮밥","sold_delta":5,"congestion_level":"혼잡","wait_minutes":15}'
```

## V4 추가 사항

- `/api/student/auth/login`: 학생 인증 MVP API
- `/api/student/auth/me`: 학생 프로필 확인 API
- `/api/ops/status`: 운영 상태 진단 API
- `/privacy`: 개인정보 처리방침 초안 페이지
- `/status`: 운영 상태 안내 페이지
- `scripts/backup_sqlite.py`: SQLite 백업 스크립트
- `RUN_DEMO_WINDOWS.ps1`: 서버 주소 자동 설정 및 백엔드 실행 스크립트
