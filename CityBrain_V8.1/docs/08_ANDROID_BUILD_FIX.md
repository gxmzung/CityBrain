# Android Build Fix V7.1

## Fixed
- Added missing `SmallBadge()` composable.
- Removed `material-icons-extended` dependency to reduce D8/R8 dex memory pressure.
- Increased Gradle/Kotlin heap settings and limited workers for Windows demo builds.

## Recommended commands
```powershell
cd C:\Users\leeyj\citybrain_demo\citybrain_release_v7_1_android_build_fix\android
.\gradlew --stop
.\gradlew clean
.\gradlew :app:assembleDebug --no-daemon --max-workers=1
```
