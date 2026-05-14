# Windows PowerShell Encoding Fix

V6.1 replaces Korean text inside `.ps1` files with ASCII-only text.

Reason:
- Windows PowerShell 5.1 can misread UTF-8 files without BOM when the console code page is not UTF-8.
- That can corrupt Korean text in strings and cause parser errors.

Fixed files:
- `RUN_DEMO_WINDOWS.ps1`
- `scripts/set_android_base_url_windows.ps1`

Recommended command:

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

For emulator:

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1 -Mode Emulator
```

For manual IP:

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1 -Ip 192.168.0.23
```
