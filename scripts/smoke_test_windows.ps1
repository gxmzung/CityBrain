# Run after backend is up.
$ErrorActionPreference = "Stop"
$base = "http://127.0.0.1:8000"
Invoke-RestMethod "$base/api/health" | ConvertTo-Json -Depth 5
Invoke-RestMethod "$base/api/student/state" | ConvertTo-Json -Depth 5
Invoke-RestMethod "$base/api/roadmap" | ConvertTo-Json -Depth 5
Write-Host "Smoke test passed" -ForegroundColor Green
