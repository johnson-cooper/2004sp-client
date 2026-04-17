@echo off
REM Build script for Lost City Desktop Client

echo === Building Lost City Desktop Client ===

cd /d E:\Client-TS-254\Client-TS-254

echo.
echo [1/4] Building client.js...
bun run build
if errorlevel 1 echo FAILED && pause && exit /b 1

echo.
echo [2/4] Copying to frontend...
copy /Y out\client.js lostcity-client\frontend\public\ >nul
copy /Y out\tinymidipcm.wasm lostcity-client\frontend\public\ >nul
copy /Y out\client.js lostcity-client\frontend\dist\ >nul
copy /Y out\tinymidipcm.wasm lostcity-client\frontend\dist\ >nul

cd lostcity-client

echo.
echo [3/4] Building Wails app...
go build -o lostcity.exe
if errorlevel 1 echo FAILED && pause && exit /b 1

echo.
echo [4/4] Copying database...
copy /Y ..\db.sqlite . >nul

echo.
echo === Build Complete ===
echo.
echo Run: lostcity-client\lostcity.exe
echo.
echo Tips:
echo   - Press ` (backquote) to toggle debug console
echo   - Make sure server is running at ws://localhost/rs2.cgi
echo.
pause