@echo off
setlocal

set "PROJECT_NAME=OverdueFree MySQL"
set "DOCKER_DIR=%~dp0docker"

cd /d "%DOCKER_DIR%"

echo [%PROJECT_NAME%] Starting Docker MySQL container...
docker compose up -d
if errorlevel 1 (
    echo [%PROJECT_NAME%] Failed to start Docker MySQL.
    pause
    exit /b 1
)

echo [%PROJECT_NAME%] MySQL is starting on port 3306.
timeout /t 3 >nul
exit /b 0
