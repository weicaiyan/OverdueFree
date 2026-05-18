@echo off
setlocal

set "PROJECT_NAME=OverdueFree Admin Web"
set "PROJECT_DIR=%~dp0"
set "PORT=5174"
set "START_COMMAND=npm run dev"

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
echo [%PROJECT_NAME%] This script does not check or release occupied ports.
start "%PROJECT_NAME%" cmd /k "cd /d ""%PROJECT_DIR%"" && %START_COMMAND%"
timeout /t 2 >nul
exit /b 0
