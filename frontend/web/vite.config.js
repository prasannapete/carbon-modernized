import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Dev-only proxy: the browser calls the SAME origin (the Vite dev server),
    // which forwards the request server-side to the leaderboard backend on :9202.
    // Because the browser request is same-origin, no CORS preflight (OPTIONS) is
    // sent, so the "CORS error / 401 preflight" against :9202 no longer occurs.
    // The forwarded request is still POST http://localhost:9202/leaderboard/get,
    // and any Authorization header is passed through unchanged.
    proxy: {
      // Leaderboard resource server.
      '/leaderboard': {
        target: 'http://localhost:9202',
        changeOrigin: true,
      },
    },
  },
})
