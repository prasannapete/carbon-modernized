import { useEffect } from 'react';
import { isAuthenticated, login } from '../lib/auth';

// Gate the whole app behind login (same idea as SRL Dashboard's AuthComponent):
// with no access token, send the browser to the local authserver's login instead
// of rendering the app. This makes the login page appear first and, after logout
// (token cleared), returns the user to login rather than a data page.
export default function RequireAuth({ children }) {
  const authed = isAuthenticated();

  useEffect(() => {
    if (!authed) login();
  }, [authed]);

  if (!authed) return null;
  return children;
}
