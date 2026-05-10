@echo off
setlocal

set "PROJECT_NAME=OverdueFree Backend"
set "PROJECT_DIR=%~dp0"
set "PORT=8080"

echo [%PROJECT_NAME%] Checking port %PORT%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$port=%PORT%; $listeners=Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue; if ($listeners) { exit 10 }"
if errorlevel 10 (
    echo [%PROJECT_NAME%] Port %PORT% is already in use. Backend may already be running.
    echo [%PROJECT_NAME%] This script will not stop or restart an existing backend process.
    timeout /t 5 >nul
    exit /b 0
)

cd /d "%PROJECT_DIR%"

echo [%PROJECT_NAME%] Starting at http://localhost:%PORT%
start "%PROJECT_NAME%" cmd /k "cd /d ""%PROJECT_DIR%"" && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 >nul
exit /b 0
