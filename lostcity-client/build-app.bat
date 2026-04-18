@echo off
cd /d E:\Client-TS-254\Client-TS-254\lostcity-client

echo [1/3] Copying files to frontend...
cd /d E:\Client-TS-254\Client-TS-254
copy /Y out\client.js lostcity-client\frontend\public\ >nul
copy /Y out\tinymidipcm.wasm lostcity-client\frontend\public\ >nul
copy /Y out\client.js lostcity-client\frontend\dist\ >nul
copy /Y out\tinymidipcm.wasm lostcity-client\frontend\dist\ >nul
echo [OK] Files copied

cd lostcity-client

echo [2/3] Embedding Windows icon into resource.syso...
if exist resource.syso del resource.syso
where goversioninfo >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo     goversioninfo not found, installing...
    go install github.com/josephspurrier/goversioninfo/cmd/goversioninfo@latest
)
goversioninfo -64 -o resource.syso
if %ERRORLEVEL% neq 0 (
    echo [WARN] goversioninfo failed, exe will have no icon
)

echo [3/3] Building Wails app...
go build -o lostcity.exe

if %ERRORLEVEL% neq 0 (
    echo [FAILED] go build exited with code %ERRORLEVEL%
    pause
    exit /b 1
)

echo [OK] lostcity.exe built
echo.
echo Run: lostcity-client\lostcity.exe
pause
