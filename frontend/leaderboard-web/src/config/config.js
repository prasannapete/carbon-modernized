// Local auth + kitkat config. Same shape/approach as SRL Dashboard's config.js,
// adapted to Vite env vars (import.meta.env / VITE_*).
const env = import.meta.env;

export const AUTH_URL = env.VITE_AUTH_URL || 'http://localhost:9201';

// Data calls go through the Vite dev proxy at /api -> kitkat-service (:9204),
// so the browser stays same-origin (no CORS) while reusing the SRL Dashboard
// bearer-token approach.
export const API_BASE = '/api';

export const OAUTH = {
  CLIENT_ID: env.VITE_OAUTH_CLIENT_ID || 'srl-analytics',
  CLIENT_SECRET: env.VITE_OAUTH_CLIENT_SECRET || 'P@ssw0rd',
  GRANT_TYPE: env.VITE_OAUTH_GRANT_TYPE || 'authorization_code',
  REDIRECT_URI: env.VITE_OAUTH_REDIRECT_URI || `${window.location.origin}/oauth2/callback`,
  SCOPE: 'openid',
};
