# CityBrain V8.1 UI Rebuild

V8.1은 V8 컨셉 이미지 기준으로 UI/UX를 다시 맞춘 코드 패키지입니다.

## 반영 사항

### Android
- 홈 화면을 컨셉 보드 기준으로 재작성
- 파란색 공식 서비스형 hero card 적용
- 주변 선택지 제거
- MY 탭 제거, 참여 탭 유지
- 하단 탭: 홈 / 실시간 / 메뉴 / 공지 / 참여
- 조식/중식/석식 시간대 선택 UI 정리
- 메뉴 보기 / 공지 확인 / 혼잡도 제보 버튼을 실제 탭 이동으로 연결
- 실시간 탭의 혼잡도 제보 chip 선택 상태 구현
- 참여 탭의 별점, 메뉴 선호 조사, 혼잡도 제보, 의견 입력 구현
- MaterialComponents / SplashScreen XML 의존 제거

### Web
- 학생 웹 화면을 V8 컨셉 보드와 같은 정보 구조로 재작성
- 단일 학생식당 서비스 구조 적용
- 주변 선택지 제거
- 오늘 메뉴 + 주간 메뉴 미리보기 추가
- 공지/참여/실시간 섹션을 한 화면에서 스크롤 탐색 가능하게 구현
- 버튼 클릭 시 섹션 이동 및 토스트 표시

### Admin
- 기존 V8 관리자 구조 유지
- 식사 시간대 관리 / 빠른 관리 액션 / 학생 참여 요약 유지

## 실행

```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_1_ui_rebuild"
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

Android Studio:

```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_1_ui_rebuild"
powershell -NoProfile -Command "Start-Process 'C:\Program Files\Android\Android Studio\bin\studio64.exe' -ArgumentList (Resolve-Path '.\android')"
```

## 주의
- 이 환경에서는 Android Gradle 빌드를 직접 실행하지 못했습니다.
- 다만 기존 오류였던 `Theme.MaterialComponents`, `Theme.CityBrain.Splash`, `material-icons-extended` 의존성은 제거했습니다.
