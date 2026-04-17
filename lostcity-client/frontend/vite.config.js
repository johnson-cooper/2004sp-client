import { defineConfig } from "vite";
import wails from "@wailsio/runtime/plugins/vite";

// Game web server URL (WEB_PORT in server .env, default 80).
// This is the HTTP server that serves /crc, /title*, /ondemand.zip, etc.
// Must match gameServerURL in main.go.
const GAME_SERVER = "http://localhost:80";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [wails("./bindings")],
  server: {
    // In dev mode the WebView talks to Vite directly (not the Go proxy),
    // so we need Vite to forward game asset requests to the game server.
    proxy: {
      "/crc":       { target: GAME_SERVER, changeOrigin: true },
      "/title":     { target: GAME_SERVER, changeOrigin: true },
      "/config":    { target: GAME_SERVER, changeOrigin: true },
      "/interface": { target: GAME_SERVER, changeOrigin: true },
      "/media":     { target: GAME_SERVER, changeOrigin: true },
      "/textures":  { target: GAME_SERVER, changeOrigin: true },
      "/wordenc":   { target: GAME_SERVER, changeOrigin: true },
      "/sounds":    { target: GAME_SERVER, changeOrigin: true },
      "/ondemand":  { target: GAME_SERVER, changeOrigin: true },
      "/build":     { target: GAME_SERVER, changeOrigin: true },
      "/maps":      { target: GAME_SERVER, changeOrigin: true },
      "/models":    { target: GAME_SERVER, changeOrigin: true },
      "/sprites":   { target: GAME_SERVER, changeOrigin: true },
    },
  },
});
