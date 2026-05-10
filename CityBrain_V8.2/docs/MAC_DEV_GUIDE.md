# Mac 개발 실행법

## 1. 서버 실행

실제 Android 폰으로 볼 때:

```bash
cd ~/citybrain_demo/citybrain_mac_v8_3_clone_work
./RUN_DEMO_MAC.sh
```

Android 에뮬레이터로 볼 때:

```bash
./RUN_DEMO_MAC.sh emulator
```

## 2. Android Studio 열기

```bash
./OPEN_ANDROID_STUDIO_MAC.sh
```

또는 직접 Android Studio에서 `android/` 폴더를 열면 됩니다.

## 3. Android 빌드

```bash
cd android
./gradlew clean assembleDebug --no-daemon --rerun-tasks
```

기기에 바로 설치:

```bash
./gradlew clean installDebug --no-daemon --rerun-tasks
```
