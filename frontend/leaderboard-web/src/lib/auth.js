import { AUTH_URL, OAUTH } from '../config/config';

// SRL Dashboard-style OAuth2 Authorization Code flow against the LOCAL authserver.
// Token stored in localStorage as e_access_token and sent as a Bearer to kitkat.
const TOKEN_KEY = 'e_access_token';

export function getToken() {
  try { return localStorage.getItem(TOKEN_KEY); } catch { return null; }
}
export function setToken(t) {
  try { localStorage.setItem(TOKEN_KEY, t); } catch { /* ignore */ }
}
export function clearToken() {
  try { localStorage.removeItem(TOKEN_KEY); } catch { /* ignore */ }
}
export function isAuthenticated() {
  return !!getToken();
}

// Redirect the browser to the authserver's authorization endpoint (same as the
// SRL Dashboard signin). The auth server saves the request and, after login,
// returns to /oauth2/callback with a code.
export function login() {
  const params = new URLSearchParams();
  params.append('response_type', 'code');
  params.append('client_id', OAUTH.CLIENT_ID);
  params.append('redirect_uri', OAUTH.REDIRECT_URI);
  params.append('scope', OAUTH.SCOPE);
  window.location.href = `${AUTH_URL}/oauth2/authorize?${params.toString()}`;
}

let redirecting = false;
// Called when a protected kitkat call returns 401 (no/expired token).
export function requireLogin() {
  if (redirecting) return;
  redirecting = true;
  clearToken();
  login();
}

// Exchange the authorization code for an access token. Basic auth with the
// client credentials, form-encoded body, via the /oauth2/token proxy (-> :9201).
export async function exchangeCode(code) {
  const data = new URLSearchParams();
  data.append('grant_type', OAUTH.GRANT_TYPE);
  data.append('code', code);
  data.append('redirect_uri', OAUTH.REDIRECT_URI);
  const basicAuth = btoa(`${OAUTH.CLIENT_ID}:${OAUTH.CLIENT_SECRET}`);

  const res = await fetch('/oauth2/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${basicAuth}`,
    },
    body: data.toString(),
  });
  if (!res.ok) throw new Error('token exchange failed: ' + res.status);
  const json = await res.json();
  setToken(json.access_token);
  return json.access_token;
}

export function logout() {
  clearToken();
  // Top-level GET navigation to the authserver's /logout ends the SSO session (a
  // cross-origin XHR can't send the SRLSESSION cookie, so a full navigation is
  // required). We pass ?redirect=<this app> so the authserver returns HERE (:3001)
  // after logout instead of the SRL Dashboard (:3000). Back on this app with no token,
  // the auth gate then shows the login screen again.
  const redirect = encodeURIComponent(`${window.location.origin}/`);
  window.location.href = `${AUTH_URL}/logout?redirect=${redirect}`;
}
