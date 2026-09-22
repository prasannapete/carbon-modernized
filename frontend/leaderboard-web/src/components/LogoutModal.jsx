import useBootstrapModal from '../lib/useBootstrapModal';
import { logout as authLogout } from '../lib/auth';

// Mirrors fragments/logout-pop-up.html. promptLogout() opened this modal; the
// "log-me-out" button now clears the local token and returns to the authserver login.
export default function LogoutModal({ open, onClose }) {
  const ref = useBootstrapModal(open, onClose);

  const logout = () => {
    authLogout();
  };

  return (
    <div className="modal" id="modal-logout-pop-up" role="dialog" style={{ zIndex: 1061 }} tabIndex={-1} ref={ref}>
      <div className="modal-dialog modal-dialog-centered modal-md" role="document">
        <div className="modal-content">
          <div className="modal-body">
            <div className="row">
              <div className="col-md-12">
                <div className="row">
                  <div className="col-md-12">
                    <div className="row purchase-analytics-section-header mb-3" style={{ fontSize: '28px' }}>
                      <span className="text-center">Are you sure you want to logout?</span>
                    </div>
                  </div>
                </div>
              </div>
              <div className="col-12 buyer-info-data mt-4 mb-5">
                <div className="d-flex justify-content-center align-items-center">
                  <div className="cancel-btn-wrapper">
                    <button className="btn log-me-out-icon" onClick={logout}>
                      <img alt="Logout" className="circled-logout-icon" style={{ width: '33px', marginLeft: '-1px' }}
                           src="/images/icons/circled-logout-icon.svg" />
                    </button>
                  </div>
                  <button className="btn close-icon" data-bs-dismiss="modal" id="shopping-list-modal-close">
                    <img className="cancel-img" src="/images/icons/cancel-button.svg" style={{ width: '33px' }} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
