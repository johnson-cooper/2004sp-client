@echo off
setlocal
REM Distribution build — Windows only (from this machine)
REM
REM  macOS and Linux require CGO + native platform toolchains.
REM  Build those on their respective platforms using:
REM
REM    macOS  : go build -o 2004sp-client-macos
REM             Then wrap in a .app bundle (see build/darwin/)
REM
REM    Linux  : go build -o 2004sp-client-linux
REM             Copy build/appicon.png and build/linux/lostcity.desktop alongside it
REM
REM  Or use GitHub Actions to build all platforms in CI.

cd /d E:\Client-TS-254\Client-TS-254\lostcity-client

if not exist "dist" mkdir dist

REM =========================================================
REM  WINDOWS (amd64)
REM =========================================================
echo [1/1] Windows amd64...

if exist resource.syso del resource.syso

where goversioninfo >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo     Installing goversioninfo...
    go install github.com/josephspurrier/goversioninfo/cmd/goversioninfo@latest
)
goversioninfo -64 -o resource.syso >nul 2>&1
if %ERRORLEVEL% neq 0 ( echo [WARN] Icon embed failed, exe will have no icon )

set GOOS=windows
set GOARCH=amd64
set CGO_ENABLED=0
go build -ldflags="-H windowsgui" -o dist\2004sp-client-windows-amd64.exe

if %ERRORLEVEL% neq 0 (
    echo [FAILED] Windows build failed
) else (
    echo [OK] dist\2004sp-client-windows-amd64.exe
)

del resource.syso >nul 2>&1

echo.
echo === Build complete ===
echo.
dir /b dist\
echo.
echo To build for macOS/Linux, run the go build command on those platforms.
echo See comments at the top of this file for instructions.
echo.
pause
