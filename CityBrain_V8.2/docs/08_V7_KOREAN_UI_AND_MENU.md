# CityBrain V7 Korean UI/UX + Weekly Menu

## 반영 범위
- 학생 화면을 한국어 중심 UI로 재구성
- 배재대 공식 앱처럼 보이도록 블루 기반 브랜드 톤 적용
- 오늘의 일품식 카드, 상태 배지, 행동 버튼, 금주 식단, 주변 선택지, 공지 영역 추가
- 버튼/칩/요일 탭/하단 탭에 터치 반응 추가
- 관리자 대시보드를 한국어 운영툴 중심으로 재구성
- Android Compose 화면도 한국어 중심으로 정리

## 금주 식단 출처
- 사용자 제공 PDF: `금주 식단.pdf`
- 기간: 2026.04.27 ~ 2026.05.01
- 학생식당 메뉴: 조식/중식 한식/중식 일품/중식 분식/석식

## 주요 수정 파일
### Backend / Web
- `backend/app/menu_data.py`
- `backend/app/routes/student.py`
- `backend/app/templates/student.html`
- `backend/app/templates/admin.html`
- `backend/app/static/app.css`
- `backend/app/static/app.js`
- `backend/app/static/manifest.json`

### Android
- `android/app/src/main/java/com/citybrain/studentapp/ui/navigation/CityBrainApp.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/home/HomeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/live/LiveScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/menu/MenuScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/notice/NoticeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/my/MyScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/theme/Color.kt`

## 새 API
- `GET /api/student/weekly-menu`

## 주의
- 실제 스토어 출시품은 아님.
- 오늘 기준 목표는 `출시 직전처럼 보이는 고품질 시연 MVP`.
- 공식 배재대 색상으로 단정하지 말고 `CityBrain 시범운영용 컬러 시스템`으로 설명해야 함.
