# CityBrain V8 실행 안내

## 서버 실행
```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_code_complete"
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

## 학생 웹
- http://127.0.0.1:8000/
- 휴대폰에서는 RUN_DEMO_WINDOWS.ps1 출력의 Phone Web 주소 사용

## 관리자
- http://127.0.0.1:8000/admin/login
- admin / change-me-now
- operator / operator123

## Android Studio
```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v8_code_complete"
powershell -NoProfile -Command "Start-Process 'C:\Program Files\Android\Android Studio\bin\studio64.exe' -ArgumentList (Resolve-Path '.\android')"
```

Android Studio에서 `Sync Now` 후 `Run`.

## V8 컨셉
- 홈: 지금 식당 가도 되는지 즉시 판단
- 실시간: 수량/혼잡/대기시간 및 혼잡도 제보
- 메뉴: 오늘 메뉴와 주간 식단
- 공지: 운영/식단/기타 공지
- 참여: 학식 평가, 메뉴 선호 조사, 제보, 의견
