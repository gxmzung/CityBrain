# CityBrain V6 Android UI + Connection Setup

## V6 목표

1. Android Jetpack Compose 화면을 V5 웹 디자인 방향과 맞춘다.
2. Windows 개발 환경에서 FastAPI 서버 주소와 Android Retrofit `BASE_URL`을 안정적으로 맞춘다.

## Android UI 동기화 내용

수정 파일:

- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/common/Common.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/home/HomeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/live/LiveScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/menu/MenuScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/screens/notice/NoticeScreen.kt`
- `android/app/src/main/java/com/citybrain/studentapp/ui/navigation/CityBrainApp.kt`

반영한 디자인 방향:

- V5 웹 학생 화면과 같은 `orange cafeteria app` 톤
- hero header, date pill, meal segmented tab, filter chip, today special card
- detail/live screen에 큰 음식 영역, status grid, preference poll, live data summary 추가
- bottom navigation label을 `Home / Live / Menu / Notices`로 정리
- 네트워크 실패 시 현재 Android `BASE_URL`을 화면에 표시해 디버깅 가능하게 함

## 서버 주소 자동 설정

### 실제 휴대폰 시연

패키지 루트에서 실행:

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

자동 수행:

1. Windows PC의 Wi-Fi/Ethernet IPv4 탐지
2. `android/gradle.properties`에 `CITYBRAIN_BASE_URL=http://PC_IP:8000/` 기록
3. FastAPI 서버를 `0.0.0.0:8000`으로 실행
4. Android Studio에서 Sync 후 Run하면 실제 휴대폰 앱이 PC 서버를 바라봄

### Android Emulator 시연

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1 -Mode Emulator
```

이 경우 Android `BASE_URL`은 다음으로 설정됨:

```text
http://10.0.2.2:8000/
```

### 서버 실행 없이 주소만 바꾸기

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\set_android_base_url_windows.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\set_android_base_url_windows.ps1 -Mode Emulator
powershell -ExecutionPolicy Bypass -File .\scripts\set_android_base_url_windows.ps1 -Ip 192.168.0.23
```

## 실행 확인

서버 실행 후:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\check_demo_connection_windows.ps1
```

브라우저 확인:

- 학생 웹: `http://127.0.0.1:8000/`
- 관리자: `http://127.0.0.1:8000/admin/login`
- 실제 폰 브라우저: `http://PC_IP:8000/`

## 주의점

- Android Studio는 `android/` 폴더를 열어야 한다.
- `RUN_DEMO_WINDOWS.ps1` 실행 후 Android Studio에서 Sync Project를 해야 최신 `BASE_URL`이 반영된다.
- 실제 휴대폰은 PC와 같은 Wi-Fi에 있어야 한다.
- Windows 방화벽이 Python/8000 포트를 막으면 휴대폰 접속이 안 될 수 있다.
