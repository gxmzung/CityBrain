# CityBrain V8 구현 변경 사항

## 핵심 변경
- `MY` 탭 제거, `참여` 탭 신설
- 주변 선택지 제거: 배재대학교 학생식당 단일 서비스에 집중
- 홈/실시간/메뉴/공지/참여 5탭 구조로 재설계
- 조식/중식/석식은 현재 시간 기준 자동 선택, 수동 선택도 가능
- 메뉴 탭은 `오늘 메뉴 + 주간 메뉴 미리보기`로 변경
- 학생 참여는 `학식 평가 / 메뉴 선호 조사 / 혼잡도 제보 / 의견 보내기`로 구체화
- 관리자 대시보드에 자동 전환/수동 변경 UI와 학생 참여 요약 반영

## 주요 수정 파일
### Web
- `backend/app/templates/student.html`
- `backend/app/templates/admin.html`
- `backend/app/static/app.css`
- `backend/app/static/app.js`

### Android
- `android/app/src/main/java/com/citybrain/studentapp/ui/navigation/CityBrainApp.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/home/HomeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/live/LiveScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/menu/MenuScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/notice/NoticeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/participate/ParticipateScreen.kt`
- `android/app/src/main/res/values/themes.xml`
- `android/app/src/main/AndroidManifest.xml`

## Android 빌드 안정화
- XML MaterialComponents 테마 의존 제거
- SplashScreen 강제 의존 제거
- `android:theme="@style/Theme.CityBrain"`로 단순화
- `material-icons-extended` 미사용 구조 유지

