import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import LogoutModal from './LogoutModal';
import { AppProvider } from '../context/AppContext';
import { postForm } from '../services/api';

// Mirrors layouts/base-layout.html shell + the keepSession() interval from common.js.
export default function Layout() {
  const [logoutOpen, setLogoutOpen] = useState(false);

  useEffect(() => {
    const id = setInterval(() => {
      postForm('/events/keep-session').catch(() => {});
    }, 30000);
    return () => clearInterval(id);
  }, []);

  return (
    <AppProvider>
      <div id="content-wrap" className="content-wrap">
        <div id="main-overlay-box" className="main-overlay-box" />
        <div id="navbar-wrap" className="navbar-wrap">
          <Header onLogout={() => setLogoutOpen(true)} />
        </div>
        <LogoutModal open={logoutOpen} onClose={() => setLogoutOpen(false)} />
        <div id="content-container" className="content-container container-fluid">
          <div id="content-container-wrap" className="content-container-wrap">
            <section>
              <Outlet />
            </section>
          </div>
        </div>
      </div>
    </AppProvider>
  );
}
