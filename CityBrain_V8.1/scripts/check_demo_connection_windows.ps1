# CityBrain V6 local API connection check
param([int]$Port = 8000)
$ErrorActionPreference = "Stop"
$Health = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/api/health" -Method Get
$State = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/api/student/state" -Method Get
Write-Host "Health OK: $($Health.ok) / $($Health.service)" -ForegroundColor Green
Write-Host "Menu: $($State.menu_name), Remaining: $($State.remaining_count), Congestion: $($State.congestion_level), Wait: $($State.wait_minutes)" -ForegroundColor Cyan
