# Build Instructions for Lost City Desktop Client

## Prerequisites

1. **Go** - Download from https://go.dev/dl/
    - Install to `C:\Program Files\Go`
    - Add to PATH: `C:\Program Files\Go\bin`

2. **Bun** - For building client.js:
    ```powershell
    powershell -Command "irm bun.sh | iex"
    ```

## Build Steps

### Step 1: Build the TypeScript Client

```bash
cd E:\Client-TS-254\Client-TS-254
bun run build
```

This creates `out/client.js`

### Step 2: Copy Files to Frontend

```bash
copy out\client.js lostcity-client\frontend\public\
copy out\tinymidipcm.wasm lostcity-client\frontend\public\
```

### Step 3: Build Wails Desktop App

```bash
cd lostcity-client
go build -o lostcity.exe
```

### Step 4: Copy Database

```bash
copy ..\db.sqlite .
```

## Run the App

```bash
lostcity-client\lostcity.exe
```

## Quick Build Script

Create `build.bat` in `E:\Client-TS-254\Client-TS-254`:

```batch
@echo off
cd /d E:\Client-TS-254\Client-TS-254

echo Building client.js...
bun run build

echo Copying to frontend...
copy out\client.js lostcity-client\frontend\public\
copy out\tinymidipcm.wasm lostcity-client\frontend\

cd lostcity-client
echo Building Wails app...
go build -o lostcity.exe

copy ..\db.sqlite .

echo Done! Run: lostcity-client\lostcity.exe
pause
```

## Troubleshooting

### Check Console Logs

- Press ` (backquote) key to toggle debug console

### Common Errors

**404 on client.js:**

- Verify file exists: `lostcity-client\frontend\public\client.js`

**Black screen:**

- Check console for errors (press ` to open)
- Make sure server is running at localhost:rs2.cgi

## File Structure

```
E:\Client-TS-254\Client-TS-254\
├── src\                    # TypeScript source
│   └── client\             # Main client code
├── out\                   # Built JS files
│   ├── client.js          # Bundled client
│   └── tinymidipcm.wasm   # Audio WASM
├── db.sqlite              # Hiscores database
└── lostcity-client\      # Wails project
    ├── frontend\
    │   └── public\
    │       └── client.js  # Copied here
    ├── lostcity.exe     # Built app
    └── db.sqlite       # Copy here too
```
