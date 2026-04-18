@echo off
cd /d E:\Client-TS-254\Client-TS-254

echo [JS] Building client.js...
bun run build

if %ERRORLEVEL% neq 0 (
    echo [FAILED] bun build exited with code %ERRORLEVEL%
    pause
    exit /b 1
)

echo [OK] client.js built successfully
pause
