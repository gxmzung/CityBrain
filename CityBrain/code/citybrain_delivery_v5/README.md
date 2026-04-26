# CityBrain Delivery V5

학생식당 운영 개선을 위한 **납품 검토용 베타 5차 확장판**입니다.

## V5 추가 기능
- 주간 운영 리포트 CSV
- 사용자 비활성화/재활성화
- 대시보드 고도화
  - 메뉴별 판매량
  - 시간대별 판매량
  - 혼잡도 분포
  - 최근 운영 요약
- 키오스크 모의 연동 API
- 설문 CSV 자동 집계
- 운영 리포트 / 감사 로그 다운로드
- Docker 배포 파일

## 실행 방법
```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload
```

## 접속
- 학생 화면: http://127.0.0.1:8000/
- 관리자 로그인: http://127.0.0.1:8000/admin/login
- API 문서: http://127.0.0.1:8000/docs

## 테스트 계정
- admin / change-me-now
- operator / operator123
- viewer / viewer123