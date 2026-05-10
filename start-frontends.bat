@echo off
setlocal

echo [OverdueFree] Starting frontend projects...

call "%~dp0frontend-h5\start-h5.bat"
if errorlevel 1 (
    echo [OverdueFree] Failed to start H5 frontend.
    pause
    exit /b 1
)

call "%~dp0admin-web\start-admin-web.bat"
if errorlevel 1 (
    echo [OverdueFree] Failed to start admin web.
    pause
    exit /b 1
)

echo [OverdueFree] Frontend projects are starting.
echo H5:        http://localhost:5173
echo Admin Web: http://localhost:5174
timeout /t 5 >nul
exit /b 0
