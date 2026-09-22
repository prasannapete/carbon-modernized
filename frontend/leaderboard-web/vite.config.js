import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// leaderboard-web now authenticates against the LOCAL Carbon Modernized authserver
// (:9201) like SRL Dashboard and calls kitkat-service (:9204) directly with a Bearer
// token — it no longer goes through the :9111 session BFF (which redirected to the
// remote authserver). Dev runs on port 3001 (SRL Dashboard already uses 3000); the
// srl-analytics client's redirect must include http://localhost:3001/oauth2/callback.
//
// Proxies keep the browser same-origin (no CORS):
//   /api/*        -> kitkat-service (:9204)   (data endpoints; /api prefix stripped)
//   /oauth2/token -> authserver     (:9201)   (authorization-code token exchange)
// The authorization redirect itself is a full-page navigation straight to :9201.
const KITKAT_TARGET = process.env.VITE_KITKAT_BASE_URL || 'http://localhost:9204';
const AUTH_TARGET = process.env.VITE_AUTH_URL || 'http://localhost:9201';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3001,
    strictPort: true,
    proxy: {
      '/api': {
        target: KITKAT_TARGET,
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api/, ''),
      },
      '/oauth2/token': {
        target: AUTH_TARGET,
        changeOrigin: true,
      },
    },
  },
});
