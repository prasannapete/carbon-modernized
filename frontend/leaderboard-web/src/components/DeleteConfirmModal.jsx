import useBootstrapModal from '../lib/useBootstrapModal';

// Mirrors fragments/delete-pop-up.html. Opened before a destructive action; the
// tick button confirms, the cancel button dismisses.
export default function DeleteConfirmModal({ open, onConfirm, onClose }) {
  const ref = useBootstrapModal(open, onClose);

  return (
    <div className="modal" id="modal-delete-pop-up" role="dialog" style={{ zIndex: 1061 }} tabIndex={-1} ref={ref}>
      <div className="modal-dialog modal-dialog-centered modal-md" role="document">
        <div className="modal-content">
          <div className="modal-body">
            <div className="row">
              <div className="col-md-12">
                <div className="row">
                  <div className="col-md-12">
                    <div className="row purchase-analytics-section-header mb-3" style={{ fontSize: '28px' }}>
                      <span className="text-center">Are you sure?</span>
                    </div>
                    <div className="row purchase-analytics-title" style={{ fontSize: '20px' }}>
                      <span className="text-center">You won't be able to revert this!</span>
                    </div>
                  </div>
                </div>
              </div>
              <div className="col-12 buyer-info-data mt-4 mb-5">
                <div className="d-flex justify-content-center align-items-center">
                  <div className="cancel-btn-wrapper">
                    <button className="btn yes-delete-it" type="button" onClick={onConfirm}>
                      <img className="save-img" src="/images/icons/submit-icon-selected.svg" />
                    </button>
                  </div>
                  <div>
                    <button className="btn close-icon delete-popup-close-button" data-bs-dismiss="modal" type="button">
                      <img className="cancel-img" src="/images/icons/cancel-button.svg" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
