# CityBrain V8.2 - 1~5 시안 복제형 UI 재구현

이 버전은 사용자가 제공한 1~5번 모바일 시안을 기준으로 Android Compose 화면을 다시 작성한 패키지입니다.

## 핵심 변경
- 기존 V8.1의 큰 파란 헤더 제거
- 홈/실시간/메뉴/공지/참여 화면을 1~5번 시안 구조에 맞춰 재작성
- 단일 학생식당 구조 유지
- 주변 선택지 제거
- MY 탭 제거
- 하단 탭: 홈 / 실시간 / 메뉴 / 공지 / 참여
- 메뉴 보기 / 공지 확인 / 혼잡도 제보 버튼 실제 탭 이동 연결
- 실시간/참여 탭의 선택 chip, 별점, 의견 입력 등 터치 상태 구현

## 실행
```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_2_pixel_clone"
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

Android Studio:
```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_2_pixel_clone"
powershell -NoProfile -Command "Start-Process 'C:\Program Files\Android\Android Studio\bin\studio64.exe' -ArgumentList (Resolve-Path '.\android')"
```

## 정직한 한계
- 외부 음식 이미지 asset이 없어서 음식 사진은 emoji 기반 placeholder입니다.
- Android 기기별 폰트/시스템바/네비게이션바 차이 때문에 100% 픽셀 동일은 불가능합니다.
- 그러나 기존 V8.1처럼 재해석하지 않고 1~5 화면 구조를 직접 따라가도록 Compose를 재작성했습니다.
