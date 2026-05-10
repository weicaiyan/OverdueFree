@echo off
setlocal

set "PROJECT_NAME=OverdueFree H5"
set "PROJECT_DIR=%~dp0"
set "PORT=5173"
set "START_COMMAND=npm run dev:h5"

echo [%PROJECT_NAME%] Restarting port %PORT% if it is already in use...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$port=%PORT%; Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | Where-Object { $_ -gt 0 } | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }"

cd /d "%PROJECT_DIR%"

if not exist "node_modules" (
    echo [%PROJECT_NAME%] node_modules not found. Running npm install...
    call npm install
    if errorlevel 1 (
        echo [%PROJECT_NAME%] npm install failed.
        pause
        exit /b 1
    )
)

echo [%PROJECT_NAME%] Starting at http://localhost:%PORT%
start "%PROJECT_NAME%" cmd /k "cd /d ""%PROJECT_DIR%"" && %START_COMMAND%"
timeout /t 2 >nul
exit /b 0
