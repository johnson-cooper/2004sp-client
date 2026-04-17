<div align="center">
    <h1>Lost City - September 7, 2004</h1>
</div>

> [!NOTE]
> Learn about our history and ethos on our forum: https://lostcity.rs/t/faq-what-is-lost-city/16

A TypeScript RuneScape 2004 game client with desktop support via Wails (Go + WebView2).

## Prerequisites

Before building, ensure you have:

- **Go** - Download from https://go.dev/dl/
  - Windows: Install to `C:\Program Files\Go` and add `C:\Program Files\Go\bin` to PATH
  - macOS/Linux: Follow Go installation guide

- **Bun** - TypeScript bundler for building client.js
  ```powershell
  # Windows
  powershell -Command "irm bun.sh | iex"
  
  # macOS/Linux
  curl -fsSL https://bun.sh/install | bash
  ```

- **2004scape Game Server** - Running on localhost (WEB_PORT=80, NODE_PORT=43594)
  - The desktop client proxies WebSocket connections to the game server
  - Download from https://github.com/2004scape/Server

## Building the TypeScript Client

The TypeScript client compiles to `out/client.js` and `out/tinymidipcm.wasm`.

```bash
cd E:\Client-TS-254\Client-TS-254  # or your project root

# Install dependencies
bun install

# Build for production
bun run build

# Build for development (with console.log preserved)
bun run build:dev
```

Output files:
- `out/client.js` - Bundled game client
- `out/client.js.map` - Source map
- `out/tinymidipcm.wasm` - MIDI audio engine (WASM binary)

## Building the Desktop Client (Wails)

The desktop client wraps the TypeScript client in a native Windows/macOS/Linux app using Wails v3.

### Quick Build (Windows)

Run the provided `build.bat` script:

```bash
cd E:\Client-TS-254\Client-TS-254
build.bat
```

This will:
1. Build the TypeScript client (`bun run build`)
2. Copy `client.js` and `tinymidipcm.wasm` to `lostcity-client/frontend/`
3. Build the Wails app (`go build -o lostcity.exe`)
4. Copy the database (`db.sqlite`)

The executable will be at: `lostcity-client/lostcity.exe`

### Manual Build

```bash
# 1. Build TypeScript client
bun run build

# 2. Copy files to frontend
copy out\client.js lostcity-client\frontend\public\
copy out\client.js lostcity-client\frontend\dist\
copy out\tinymidipcm.wasm lostcity-client\frontend\public\
copy out\tinymidipcm.wasm lostcity-client\frontend\dist\

# 3. Build Wails desktop app
cd lostcity-client
go build -o lostcity.exe

# 4. Copy database
copy ..\db.sqlite .

# 5. Run the app
lostcity-client\lostcity.exe
```

## Development

### Running in Wails Dev Mode

For hot-reload during development (runs TypeScript client in browser via Vite):

```bash
cd lostcity-client
go run .
```

This starts a dev server on `http://localhost:34115` with hot-reload enabled.

### Debug Console

While running the app, press `` ` `` (backquote) to toggle the debug console and view logs.

## Architecture

```
┌─────────────────────────────────────────────┐
│        Wails Desktop App (Go)               │
├─────────────────────────────────────────────┤
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │   WebView2 (Embedded Browser)        │  │
│  │  ┌──────────────────────────────────┐  │
│  │  │  Frontend: HTML + TypeScript      │  │
│  │  │  ├── client.js (game client)     │  │
│  │  │  └── tinymidipcm.wasm (audio)    │  │
│  │  └──────────────────────────────────┘  │
│  └──────────────────────────────────────┘  │
│                                             │
│  Go Proxy Servers:                          │
│  ├── HTTP Asset Proxy (port 80 → WEB_PORT) │
│  ├── WebSocket Proxy (port 43595)          │
│  └── Embedded Asset Server (/)             │
└─────────────────────────────────────────────┘
              ↓
        2004scape Game Server
        (WEB_PORT=80, NODE_PORT=43594)
```

### Network Flow

1. **Assets** (CRC, config, textures, etc.)
   - Browser requests `/crc`, `/config`, etc.
   - Wails Go app checks if file is embedded in `frontend/dist/`
   - If embedded: serve from embedded FS
   - If not: proxy to game server (port 80)

2. **WebSocket (Game Connection)**
   - Browser connects to `ws://localhost:43595` (Go proxy)
   - Go proxy dials `ws://localhost:80` (game server WebSocket bridge)
   - Bidirectional message forwarding

This avoids Origin header issues with the game server.

## Troubleshooting

### Black Screen / Client Won't Load

- Check debug console (press `` ` ``)
- Verify game server is running and accessible at `http://localhost:80`
- Ensure `client.js` and `tinymidipcm.wasm` are in `lostcity-client/frontend/dist/`

### WebSocket Connection Failed

- Verify game server is running (`WEB_PORT=80`)
- Check that the WebSocket bridge is active on the game server
- Ensure port 43595 is not blocked on your machine

### No Sound / Audio Not Working

- Verify `tinymidipcm.wasm` exists in `lostcity-client/frontend/dist/`
- Ensure `SCC1_Florestan.sf2` soundfont is present in the same directory
- Check debug console for audio-related errors

## Project Structure

```
├── src/                              # TypeScript source
│   ├── client/                       # Main game client
│   ├── 3rdparty/                     # Audio (tinymidipcm.js, audio.js)
│   ├── dash3d/                       # 3D map viewer
│   └── ...
├── out/                              # Build output
│   ├── client.js                     # Bundled client
│   ├── tinymidipcm.wasm              # WASM audio binary
│   └── *.js.map                      # Source maps
├── lostcity-client/                  # Wails desktop app
│   ├── main.go                       # Go entry point
│   ├── hiscores.go                   # Hiscores service
│   ├── frontend/                     # Frontend assets
│   │   ├── public/                   # Development assets
│   │   ├── dist/                     # Production embedded assets
│   │   ├── index.html                # HTML entry point
│   │   └── vite.config.js            # Vite dev config
│   ├── build.bat                     # Build script
│   └── lostcity.exe                  # Built app (after build)
├── db.sqlite                         # Hiscores database
├── bundle.ts                         # Bun bundler config
├── build.bat                         # Master build script
└── package.json
```

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT). See the [LICENSE](LICENSE) file for details.
