# CityBrain Release V6

## 변경 요약

- Android Compose 화면을 V5 웹 디자인 톤으로 재구성
- 학생 Home / Live / Menu / Notices 화면 개선
- 서버 주소 자동 설정 스크립트 보강
- Android Studio에서 열 수 있도록 `settings.gradle.kts` 추가
- Retrofit `BASE_URL`이 `android/gradle.properties`의 `CITYBRAIN_BASE_URL`을 바라보도록 정리

## 가장 쉬운 실행

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

그 다음:

1. Android Studio에서 `android/` 폴더 열기
2. Sync Project
3. 실제 휴대폰 또는 에뮬레이터 Run

에뮬레이터를 쓸 때:

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1 -Mode Emulator
```
