@echo off
setlocal

set "PROJECT_NAME=OverdueFree Backend"
set "PROJECT_DIR=%~dp0"
set "PORT=8080"
set "HEALTH_URL=http://localhost:%PORT%/api/health"

echo [%PROJECT_NAME%] Checking port %PORT%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$port=%PORT%; $listeners=Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue; if ($listeners) { exit 10 }"
if errorlevel 10 (
    echo [%PROJECT_NAME%] Port %PORT% is already in use. Backend may already be running.
    echo [%PROJECT_NAME%] This script will not stop or restart an existing backend process.
    call :waitForHealth
    if errorlevel 1 (
        echo [%PROJECT_NAME%] Port %PORT% is occupied, but backend health check did not pass.
        echo [%PROJECT_NAME%] Please check whether the existing process is OverdueFree Backend.
        pause
        exit /b 1
    )
    echo [%PROJECT_NAME%] Existing backend is ready.
    exit /b 0
)

cd /d "%PROJECT_DIR%"

echo [%PROJECT_NAME%] Starting at http://localhost:%PORT%
start "%PROJECT_NAME%" cmd /k "cd /d ""%PROJECT_DIR%"" && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
call :waitForHealth
if errorlevel 1 (
    echo [%PROJECT_NAME%] Backend did not become ready in time.
    echo [%PROJECT_NAME%] Check the backend command window for Maven, JDK, or database errors.
    pause
    exit /b 1
)
echo [%PROJECT_NAME%] Backend is ready.
exit /b 0

:waitForHealth
echo [%PROJECT_NAME%] Waiting for backend health check...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$deadline=(Get-Date).AddSeconds(120); do { try { $response=Invoke-RestMethod -Uri '%HEALTH_URL%' -Method Get -TimeoutSec 3; if ($response.code -eq 0 -and $response.data.overallStatus -eq 'UP') { exit 0 } } catch { }; Start-Sleep -Seconds 2 } while ((Get-Date) -lt $deadline); exit 1"
exit /b %ERRORLEVEL%
