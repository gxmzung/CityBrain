# CityBrain V5 UI Updates

## 이번 V5에서 반영한 것
- 학생 웹 화면을 `카페/배달앱 느낌 + 공식 서비스 톤`으로 재정렬
- 상단 헤더, 날짜 pill, meal tab, chip filter, 오늘의 일품식 hero card, 주변 식당 카드 구조로 재설계
- 관리자 화면을 `학교 납품용 대시보드` 느낌으로 재설계
- KPI 카드, Quick Action, 운영 반영 폼, 설문/공지/로그 영역을 카드 시스템으로 통일
- rounded radius, shadow, spacing, color token을 하나의 디자인 시스템으로 정리

## 주요 수정 파일
- `backend/app/templates/student.html`
- `backend/app/templates/admin.html`
- `backend/app/static/app.css`

## 디자인 의도
- 학생 화면: 3초 안에 `남은 수량 / 혼잡도 / 예상 대기시간`이 읽히게 함
- 관리자 화면: 시연 때 `현재 상태 확인 → 버튼 한 번 → 학생 화면 변화`가 바로 보이게 함
- 전체 톤: 배재대 공식 서비스처럼 과장되지 않고, 데모여도 충분히 정리된 인상을 주는 방향

## 아직 남는 과제
- Android Compose 화면을 웹 V5와 1:1 토큰 수준으로 추가 동기화
- 실제 앱 아이콘 PNG / adaptive icon / splash asset 확정
- 타이포그래피 폰트(예: Pretendard) 번들링 여부 결정
- 접근성 QA (대비, 포커스, 터치 타겟) 점검
