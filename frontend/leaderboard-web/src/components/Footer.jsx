// Mirrors fragments/footer.html (the footer contents are display:none in the
// reference app, preserved as-is).
export default function Footer() {
  return (
    <footer className="kt-footer"
            style={{ bottom: 0, position: 'fixed', fontSize: '20px', width: '1900px', display: 'flex', justifyContent: 'center' }}>
      <div className="container-fluid">
        <div className="footer-contents" style={{ display: 'none' }}>
          <div className="row">
            <div className="col-xl-4 col-lg-6">
              <div>
                <div>
                  <a href="https://kitchentandem.com/privacy-policy.html" target="_blank" rel="noreferrer">
                    <span data-i18n="footer-privacy-policy-text" className="text-muted">Privacy Policy</span>
                  </a>
                  <span>|</span>
                  <a href="https://kitchentandem.com/terms-and-conditions.html" target="_blank" rel="noreferrer">
                    <span data-i18n="footer-terms-and-conditions-text" className="text-muted">Terms &amp; Conditions</span>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
