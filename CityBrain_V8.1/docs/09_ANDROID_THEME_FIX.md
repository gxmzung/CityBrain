# V7.4 Android Theme Fix

## Problem
`Theme.MaterialComponents.DayNight.NoActionBar` was referenced in `themes.xml`, but the Material Components XML dependency was removed to reduce D8 memory usage.

## Fix
`Theme.CityBrain` now inherits from the Android platform theme:

```xml
parent="android:style/Theme.Material.Light.NoActionBar"
```

This keeps the app Compose-only and avoids re-adding `com.google.android.material:material`.

## Modified file
- `android/app/src/main/res/values/themes.xml`
