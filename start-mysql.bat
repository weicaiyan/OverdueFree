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

echo [%PROJECT_NAME%] Waiting for MySQL to accept connections...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$deadline=(Get-Date).AddSeconds(90); do { docker exec overduefree-mysql mysqladmin ping -uroot -poverduefree_root --silent *> $null; if ($LASTEXITCODE -eq 0) { exit 0 }; Start-Sleep -Seconds 2 } while ((Get-Date) -lt $deadline); exit 1"
if errorlevel 1 (
    echo [%PROJECT_NAME%] MySQL did not become ready in time. Check Docker Desktop and the overduefree-mysql container logs.
    pause
    exit /b 1
)

echo [%PROJECT_NAME%] MySQL is ready on port 3306.
exit /b 0
