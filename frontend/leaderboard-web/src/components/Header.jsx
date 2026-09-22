import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postForm } from '../services/api';
import { useApp } from '../context/AppContext';
import Swal, { CONFIRM_BTN } from '../lib/swal';

// Mirrors fragments/header.html + the getAllEvents() chrome from common.js.
// The hamburger + "Events" dropdowns are React-controlled (open state) rather than
// relying on Bootstrap's data-API, so they open reliably in the SPA.
export default function Header({ onLogout }) {
  const navigate = useNavigate();
  const { leadPagination } = useApp();
  const [events, setEvents] = useState([]);
  const [menuOpen, setMenuOpen] = useState(false);
  const [eventsOpen, setEventsOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    let cancelled = false;
    postForm('/events/get-all')
      .then((response) => {
        if (cancelled || !response || !response.success) return;
        const data = response.data || [];
        setEvents(data);
        if (data.length > 0 && data[0].primaryColor) {
          document.documentElement.style.backgroundColor = data[0].primaryColor;
        }
      })
      .catch((data) => {
        if (data && data.message === 'unauthorized') return;
        Swal.fire({ title: 'Something went wrong', text: data && data.d, confirmButtonText: CONFIRM_BTN });
      });
    return () => { cancelled = true; };
  }, []);

  // Close the menus when clicking outside them.
  useEffect(() => {
    const onDocClick = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
        setEventsOpen(false);
      }
    };
    document.addEventListener('click', onDocClick);
    return () => document.removeEventListener('click', onDocClick);
  }, []);

  const go = (path) => {
    setMenuOpen(false);
    setEventsOpen(false);
    navigate(path);
  };

  const selectEvent = (ev) => {
    localStorage.setItem('selectedEventId', ev.id);
    if (ev.primaryColor) {
      document.documentElement.style.backgroundColor = ev.primaryColor;
      const cw = document.querySelector('.content-wrap');
      if (cw) cw.style.backgroundColor = ev.primaryColor;
    }
    go(`/event-scores/single-event-display-score?id=${ev.id}`);
  };

  return (
    <div className="navbar-container container-fluid kt-header-navbar-container main-header">
      <div className={`dropdown${menuOpen ? ' show' : ''}`} ref={menuRef}>
        <ul className="dropdown-menu1 hamburger-action-dropdown" aria-labelledby="kt-nav-hamburger-menu">
          <li style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div className="navbar-container container-fluid kt-header-navbar-container">
              <div className={`dropdown${menuOpen ? ' show' : ''}`}>
                <button className="navbar-hamburger" type="button" id="kt-nav-hamburger-menu"
                        aria-expanded={menuOpen}
                        style={{ border: 'none', backgroundColor: '#FFFFFF' }}
                        onClick={(e) => { e.stopPropagation(); setMenuOpen((o) => !o); setEventsOpen(false); }}>
                  <span className="hamburger-action-icon fa fa-bars" style={{ listStyle: 'none', fontSize: '30px' }} />
                </button>
                <ul className={`dropdown-menu hamburger-action-dropdown${menuOpen ? ' show' : ''}`}
                    aria-labelledby="nav-hamburger-menu">
                  <li>
                    <a href="/events/list" className="dropdown-item header-text"
                       onClick={(e) => { e.preventDefault(); go('/events/list'); }}>
                      <span>Event list</span>
                    </a>
                  </li>
                  <li>
                    <a href="/events/create-event" className="dropdown-item header-text"
                       onClick={(e) => { e.preventDefault(); go('/events/create-event'); }}>
                      <span>Create event</span>
                    </a>
                  </li>
                  <li>
                    <a href="/event-scores/event-display-score" className="dropdown-item header-text"
                       onClick={(e) => { e.preventDefault(); go('/event-scores/event-display-score'); }}>
                      <span>Display score</span>
                    </a>
                  </li>
                  <li className="dropdown-dashboard-section">
                    <button className="dropdown-item" type="button"
                            onClick={(e) => { e.stopPropagation(); setEventsOpen((o) => !o); }}>
                      <span className="header-text">Events</span>
                      <span className="user-profile header-help dropdown" style={{ padding: '0px 0px' }}>
                        <span aria-expanded={eventsOpen} aria-haspopup="true"
                              className="waves-effect waves-light help-selection" id="header-dashboard-selection"
                              role="button">
                          <span className="m-l-5" id="dashboard-selection" style={{ display: 'none' }}>English</span>
                          <i className="ti-angle-down" />
                        </span>
                        <ul className={`show-notification profile-notification event-name-wrapper dropdown-menu dropdown-menu-right${eventsOpen ? ' show' : ''}`}
                            id="header-dashboard-selection-menu">
                          {events.map((ev) => (
                            <li key={ev.id} className="event-name-list" id={ev.id}
                                data-score-type={ev.scoreType} data-primary-color={ev.primaryColor}
                                data-uploaded-logo={ev.logoPath}
                                onClick={(e) => { e.stopPropagation(); selectEvent(ev); }} style={{ cursor: 'pointer' }}>
                              {ev.eventName}
                            </li>
                          ))}
                        </ul>
                      </span>
                    </button>
                  </li>
                  <li>
                    <div className="d-flex justify-content-center col-12 align-items-center display-pagination-section">
                      {[10, 20, 30].map((n) => (
                        <button key={n} type="button" className="btn btn-add-user1 btn-pagination"
                                value={n} id={`load-${n}`} style={{ cursor: 'pointer' }}
                                onClick={(e) => { e.stopPropagation(); leadPagination(n); }}>
                          <span className="pagination-btn" style={{ textTransform: 'none' }}>{n}</span>
                        </button>
                      ))}
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <a className="header-text" style={{ width: '100%' }}>
              <img alt="Logout" className="hamburger-action-icon main-logo-icon"
                   style={{ height: '55px', listStyle: 'none' }} src="/images/logos/carbonLogo.png" />
            </a>
            <ul className="nav-center d-flex justify-content-center col-1">
              <li>
                <a href="#" className="header-text" onClick={(e) => { e.preventDefault(); onLogout(); }}>
                  <img alt="Logout" className="hamburger-action-icon" src="/images/icons/logout-icon.svg" />
                </a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  );
}
