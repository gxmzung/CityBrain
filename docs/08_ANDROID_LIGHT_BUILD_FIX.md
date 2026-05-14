# CityBrain V7.3 Android Light Build Fix

## Why this version exists
Previous V7 builds could fail on Windows Android Studio because:

1. `android.useAndroidX=true` was missing after some script runs.
2. `material-icons-extended` and `com.google.android.material:material` were too heavy for D8 dex merging on low-memory Gradle settings.
3. `RUN_DEMO_WINDOWS.ps1` overwrote `gradle.properties` and removed memory settings.

## What V7.3 changed
- Removed heavy icon/material dependencies from `android/app/build.gradle.kts`.
- Replaced Compose icon usage with text/emoji based lightweight UI.
- Added `SmallBadge()` to `Common.kt`.
- Fixed `RUN_DEMO_WINDOWS.ps1` so it writes full Gradle memory + AndroidX settings.
- Fixed `scripts/set_android_base_url_windows.ps1` the same way.

## Recommended path
Extract this project to an ASCII-only path, for example:

```powershell
C:\Users\leeyj\citybrain_demo\citybrain_release_v7_3_android_light_stable
```

## Run server
```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v7_3_android_light_stable"
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

## Open Android Studio
Use a second PowerShell window:

```powershell
cd "C:\Users\leeyj\citybrain_demo\citybrain_release_v7_3_android_light_stable"
powershell -NoProfile -Command "Start-Process 'C:\Program Files\Android\Android Studio\bin\studio64.exe' -ArgumentList (Resolve-Path '.\android')"
```

Then Android Studio > Sync Project > Run.

## If Android Studio still uses old cache
Android Studio > File > Invalidate Caches > Invalidate and Restart.
