@echo off
setlocal

echo [OverdueFree] Starting local demo...

call "%~dp0start-mysql.bat"
if errorlevel 1 (
    echo [OverdueFree] Failed to start MySQL.
    pause
    exit /b 1
)

call "%~dp0backend\start-backend.bat"
if errorlevel 1 (
    echo [OverdueFree] Failed to start backend.
    pause
    exit /b 1
)

call "%~dp0start-frontends.bat"
if errorlevel 1 (
    echo [OverdueFree] Failed to start frontend projects.
    pause
    exit /b 1
)

echo [OverdueFree] Local demo entry points:
echo Backend:   http://localhost:8080
echo H5:        http://localhost:5173
echo Admin Web: http://localhost:5174
timeout /t 5 >nul
exit /b 0
