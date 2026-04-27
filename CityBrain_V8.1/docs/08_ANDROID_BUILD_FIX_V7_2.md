# Android Build Fix V7.2

This package reduces Android dex memory pressure.

Changes:
- Removed com.google.android.material dependency.
- Removed debug ui-tooling and ui-test-manifest dependencies.
- Kept android.useAndroidX=true.
- Disabled Gradle daemon for low-memory Windows builds.
- Increased JVM heap settings.
- Common.kt does not use material-icons-extended.

Build command:

```powershell
cd android
.\gradlew --stop
Remove-Item -Recurse -Force .\.gradle -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force .\build -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force .\app\build -ErrorAction SilentlyContinue
.\gradlew :app:assembleDebug --no-daemon
```
