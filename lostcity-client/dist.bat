@echo off
setlocal
REM Distribution build — produces platform packages in dist\
REM   Windows : dist\lostcity-windows-amd64.exe  (icon embedded)
REM   macOS   : dist\lostcity-darwin-amd64.app\  (app bundle with .icns)
REM             dist\lostcity-darwin-arm64.app\
REM   Linux   : dist\lostcity-linux-amd64\       (binary + icon.png + .desktop)

cd /d E:\Client-TS-254\Client-TS-254\lostcity-client

if not exist "dist" mkdir dist

REM =========================================================
REM  1. WINDOWS (amd64)
REM =========================================================
echo [1/4] Windows amd64...

REM Generate resource.syso to embed icon.ico + manifest into the exe
where goversioninfo >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo     Installing goversioninfo...
    go install github.com/josephspurrier/goversioninfo/cmd/goversioninfo@latest
)
goversioninfo -o resource.syso >nul 2>&1

set GOOS=windows
set GOARCH=amd64
set CGO_ENABLED=0
go build -ldflags="-H windowsgui" -o dist\2004sp-client-windows-amd64.exe
if %ERRORLEVEL% neq 0 ( echo [FAILED] Windows ) else ( echo [OK] dist\lostcity-windows-amd64.exe )

REM Remove resource.syso before non-Windows builds
del resource.syso >nul 2>&1

REM =========================================================
REM  2. macOS (amd64 — Intel)
REM =========================================================
echo [2/4] macOS amd64 (Intel)...
set GOOS=darwin
set GOARCH=amd64
set CGO_ENABLED=0
go build -o dist\2004sp-client-macos-amd64-bin

if %ERRORLEVEL% neq 0 (
    echo [FAILED] macOS Intel
) else (
    call :make_app_bundle dist\lostcity-darwin-amd64.app dist\lostcity-macos-amd64-bin
    del dist\lostcity-macos-amd64-bin >nul 2>&1
    echo [OK] dist\lostcity-darwin-amd64.app\
)

REM =========================================================
REM  3. macOS (arm64 — Apple Silicon)
REM =========================================================
echo [3/4] macOS arm64 (Apple Silicon)...
set GOOS=darwin
set GOARCH=arm64
set CGO_ENABLED=0
go build -o dist\2004sp-client-macos-arm64-bin

if %ERRORLEVEL% neq 0 (
    echo [FAILED] macOS arm64
) else (
    call :make_app_bundle dist\lostcity-darwin-arm64.app dist\lostcity-macos-arm64-bin
    del dist\lostcity-macos-arm64-bin >nul 2>&1
    echo [OK] dist\lostcity-darwin-arm64.app\
)

REM =========================================================
REM  4. Linux (amd64)
REM =========================================================
echo [4/4] Linux amd64...
set GOOS=linux
set GOARCH=amd64
set CGO_ENABLED=0
go build -o dist\2004sp-client-linux-amd64\lostcity

if %ERRORLEVEL% neq 0 (
    echo [FAILED] Linux
) else (
    REM Copy icon (use master 1024x1024 PNG — Linux DEs scale it)
    copy /Y build\appicon.png dist\lostcity-linux-amd64\icon.png >nul

    REM Write a .desktop file so the app appears in Linux app launchers
    (
        echo [Desktop Entry]
        echo Version=1.0
        echo Name=2004scape
        echo Comment=Lost City MMO Client
        echo Exec=./lostcity
        echo Icon=./icon.png
        echo Terminal=false
        echo Type=Application
        echo Categories=Game;
        echo StartupWMClass=lostcity
    ) > dist\lostcity-linux-amd64\lostcity.desktop

    echo [OK] dist\lostcity-linux-amd64\
)

echo.
echo === Distribution builds complete ===
echo.
dir /b dist\
echo.
pause
exit /b 0

REM =========================================================
REM  Subroutine: make_app_bundle <output.app> <binary>
REM  Builds a minimal macOS .app bundle with icons.icns
REM =========================================================
:make_app_bundle
set APP_DIR=%~1
set BIN_SRC=%~2

if exist "%APP_DIR%" rmdir /s /q "%APP_DIR%"
mkdir "%APP_DIR%\Contents\MacOS"
mkdir "%APP_DIR%\Contents\Resources"

copy /Y "%BIN_SRC%" "%APP_DIR%\Contents\MacOS\lostcity" >nul
copy /Y build\darwin\Info.plist "%APP_DIR%\Contents\" >nul
copy /Y build\darwin\icons.icns "%APP_DIR%\Contents\Resources\" >nul
exit /b 0
