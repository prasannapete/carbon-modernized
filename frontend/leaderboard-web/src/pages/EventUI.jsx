import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import moment from 'moment';
import { postForm, postJson, postMultipart } from '../services/api';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import Footer from '../components/Footer';

const GLOBAL_DATE_FORMAT = 'YYYY-MM-DD';
const ALERT_DISPLAY_TIME = 1500;

function formatDate(value) {
  return value ? moment(value).format(GLOBAL_DATE_FORMAT) : '';
}

// Mirrors event-ui.html + event-ui.js (create/edit event form, logo upload).
export default function EventUI() {
  const navigate = useNavigate();
  const { id: routeId } = useParams();
  const id = routeId || '';

  const [eventName, setEventName] = useState('');
  const [eventType, setEventType] = useState('');
  const [description, setDescription] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [scoreType, setScoreType] = useState({ value: '-1', label: 'Score type' });
  const [scoreOpen, setScoreOpen] = useState(false);
  const scoreRef = useRef(null);
  const [primaryColor, setPrimaryColor] = useState('');
  const [isLive, setIsLive] = useState(false);
  const [logoPath, setLogoPath] = useState(null);
  const [errors, setErrors] = useState({});
  const fileInputRef = useRef(null);

  // Earliest selectable date is today: the date pickers must not allow past dates.
  const todayStr = moment().format(GLOBAL_DATE_FORMAT);

  useEffect(() => {
    if (id !== '') {
      postForm('/events/get', { id })
        .then((response) => {
          if (response && response.success) {
            const d = response.data;
            setEventName(d.eventName || '');
            setEventType(d.eventType || '');
            setDescription(d.description || '');
            setStartDate(formatDate(d.startDate));
            setEndDate(formatDate(d.endDate));
            setScoreType({ value: String(d.scoreType), label: d.scoreType == 0 ? 'Points' : 'Time' });
            setPrimaryColor(d.primaryColor || '');
            setLogoPath(d.logoPath != null ? d.logoPath : null);
            setIsLive(!!d.isLive);
          }
        })
        .catch((data) => {
          Swal.fire({
            title: 'Something went wrong',
            confirmButtonText: CONFIRM_BTN,
            text: data && data.d,
            timer: ALERT_DISPLAY_TIME,
          });
        });
    }
  }, [id]);

  // Close the Score type dropdown when clicking outside it.
  useEffect(() => {
    const onDocClick = (e) => {
      if (scoreRef.current && !scoreRef.current.contains(e.target)) setScoreOpen(false);
    };
    document.addEventListener('click', onDocClick);
    return () => document.removeEventListener('click', onDocClick);
  }, []);

  const uploadLogo = (file) => {
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    postMultipart('/events/upload-logo', formData)
      .then((response) => {
        if (Array.isArray(response) && response[0] && response[0].success) {
          setLogoPath(response[0].excelUploadPath);
        }
      })
      .catch(() => {});
  };

  const onDrop = (e) => {
    e.preventDefault();
    const file = e.dataTransfer.files && e.dataTransfer.files[0];
    uploadLogo(file);
  };

  const validate = () => {
    const next = {};
    if (!eventName.trim()) next.eventName = 'Event name is required';
    if (!primaryColor.trim()) next.primaryColor = 'Primary color is required';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const onSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    const formdata = {
      eventName: eventName.trim(),
      eventType: eventType.trim(),
      description: description.trim(),
      startDateStr: startDate,
      endDateStr: endDate,
      primaryColor: primaryColor.trim(),
      file: '',
      isLive: isLive ? 1 : 0,
      scoreType: scoreType.value,
      logoPath,
    };
    if (id !== '') formdata.id = id;

    postJson('/events/save', formdata)
      .then((data) => {
        if (data && data.success) {
          Swal.fire({
            title: 'Success',
            text: 'Saved successfully',
            confirmButtonText: CONFIRM_BTN,
            allowOutsideClick: false,
            timer: 1500,
          });
          navigate('/events/list');
        } else {
          Swal.fire({
            title: 'Something went wrong',
            text: data && data.error,
            confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px">',
          });
        }
      })
      .catch((data) => {
        Swal.fire({
          title: 'Something went wrong',
          text: data && data.d,
          confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px">',
        });
      });
  };

  const onCancel = () => {
    navigate('/events/list');
  };

  return (
    <section>
      <div className="main-container">
        <input name="id" type="hidden" id="id" value={id} readOnly />
        <div className="create-event-container d-flex justify-content-center">
          <div className="row" style={{ paddingLeft: '20px', paddingRight: '20px' }}>
            <form className="form-supplier-order-status-change" id="create-events-form" method="POST" onSubmit={onSubmit}>
              <div className="col-12 create-event-contents">
                <div className="purchase-analytics-title form-primary"> Event name</div>
                <div className="d-flex justify-content-start align-items-center add-participant-modal-input-section">
                  <div className="flex-column append-validation-error">
                    <input className="purchase-analytics-value d-flex buyer-info-dropdown-content create-event-input"
                           id="eventName" name="eventName" value={eventName} onChange={(e) => setEventName(e.target.value)} />
                    {errors.eventName && <div className="field-validation-error">{errors.eventName}</div>}
                  </div>
                </div>
                <div className="append-validation-error d-flex justify-content-end align-items-center" />
              </div>

              <div className="col-12 create-event-contents">
                <div className="purchase-analytics-title form-primary"> Event type</div>
                <div className="d-flex justify-content-start align-items-center add-participant-modal-input-section">
                  <div className="flex-column append-validation-error">
                    <input className="purchase-analytics-value d-flex buyer-info-dropdown-content create-event-input"
                           id="eventType" name="eventType" value={eventType} onChange={(e) => setEventType(e.target.value)} />
                  </div>
                </div>
                <div className="append-validation-error d-flex justify-content-end align-items-center" />
              </div>

              <div className="col-12 create-event-contents">
                <div className="purchase-analytics-title form-primary"> Description</div>
                <div className="d-flex justify-content-start align-items-center add-participant-modal-input-section">
                  <div className="flex-column append-validation-error">
                    <textarea className="purchase-analytics-value d-flex buyer-info-dropdown-content create-event-input create-event-input-text-area"
                              id="description" name="description" value={description} onChange={(e) => setDescription(e.target.value)} />
                  </div>
                </div>
                <div className="append-validation-error d-flex justify-content-end align-items-center" />
              </div>

              <div className="col-lg-12">
                <div className="form-group">
                  <label className="purchase-analytics-title">Start date</label>
                  <div className="input-append purchase-analytics-value" id="eventStartDate">
                    <input autoComplete="off" className="events-input-date" id="startDate" name="startDateStr"
                           type="date" min={todayStr} value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                    <span className="add-on right-align-icon">
                      <img className="calender-img" style={{ cursor: 'pointer' }} src="/images/icons/calender-logo.svg" />
                    </span>
                  </div>
                </div>
              </div>

              <div className="col-lg-12">
                <div className="form-group">
                  <label className="purchase-analytics-title">End date</label>
                  <div className="input-append purchase-analytics-value" id="eventEndDate">
                    <input autoComplete="off" className="events-input-date" id="endDate" name="endDateStr"
                           type="date" min={startDate || todayStr} value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                    <span className="add-on right-align-icon">
                      <img className="calender-img" style={{ cursor: 'pointer' }} src="/images/icons/calender-logo.svg" />
                    </span>
                  </div>
                </div>
              </div>

              <div className="col-12 create-event-contents">
                <div className="purchase-analytics-title form-primary"> Score type</div>
                <div className="d-flex justify-content-start align-items-center">
                  <div className="d-flex justify-content-start align-items-center flex-fill">
                    <div className="flex-column append-validation-error">
                      <div className="col-1">
                        <div id="btn-select-order-status" className={`list-selection${scoreOpen ? ' show' : ''}`} ref={scoreRef}>
                          <div className={`dropdown-toggle score-type-dropdowns${scoreOpen ? ' show' : ''}`}
                               role="button" aria-haspopup="true" aria-expanded={scoreOpen}
                               onClick={(e) => { e.stopPropagation(); setScoreOpen((o) => !o); }}>
                            <div className="btn-current-selection my-orders-header-content"
                                 style={{ display: 'flex', justifyContent: 'space-between' }}>
                              <span className="current-selection current-selection-columns" value={scoreType.value}>
                                {scoreType.label}</span>
                              <span className="ti-angle-down" />
                            </div>
                            <div className={`dropdown-menu dropdown-menu-left score-type-dropdown score-type${scoreOpen ? ' show' : ''}`}>
                              <a className="dropdown-item header-content-dropdown-items" value="0"
                                 onClick={(e) => { e.stopPropagation(); setScoreType({ value: '0', label: 'Points' }); setScoreOpen(false); }}>
                                <div className="select-dropdown-items-options"><div value="0">Points</div></div>
                              </a>
                              <a className="dropdown-item header-content-dropdown-items" value="1"
                                 onClick={(e) => { e.stopPropagation(); setScoreType({ value: '1', label: 'Time' }); setScoreOpen(false); }}>
                                <div className="select-dropdown-items-options"><div value="1">Time</div></div>
                              </a>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="col-12 create-event-contents">
                <div className="purchase-analytics-title form-primary">Primary colour</div>
                <div className="d-flex justify-content-start align-items-center add-participant-modal-input-section">
                  <div className="flex-column append-validation-error">
                    <input className="purchase-analytics-value d-flex buyer-info-dropdown-content create-event-input"
                           type="color" id="primaryColor" name="primaryColor"
                           value={primaryColor || '#000000'} onChange={(e) => setPrimaryColor(e.target.value)} />
                    {errors.primaryColor && <div className="field-validation-error">{errors.primaryColor}</div>}
                  </div>
                </div>
                <div className="append-validation-error d-flex justify-content-end align-items-center" />
              </div>

              <div className="col-12">
                <div className="form-group form-default form-static-label">
                  <input type="hidden" className="form-control" name="file" id="logoPath" />
                  <label className="float-label">Background logo</label>
                  <input ref={fileInputRef} type="file" accept="image/*" style={{ display: 'none' }}
                         onChange={(e) => uploadLogo(e.target.files && e.target.files[0])} />
                  <div id="import-logo-photo" className="dropzone d-flex justify-content-center"
                       style={{ cursor: 'pointer' }}
                       onClick={() => fileInputRef.current && fileInputRef.current.click()}
                       onDragOver={(e) => e.preventDefault()} onDrop={onDrop}>
                    <div className="dz-message icon-uploaded" style={{ display: logoPath != null ? '' : 'none' }}>
                      <div className="mb-3">
                        <span className="d-flex justify-content-center">
                          <img className="uploaded-logo" src={logoPath || ''} style={{ width: '150px' }} />
                        </span>
                      </div>
                      <p>Drop files here to change background logo.</p>
                    </div>
                    <div className="dz-message icon-not-uploaded" style={{ display: logoPath != null ? 'none' : '' }}>
                      <div className="mb-3">
                        <span className="fa fa-image fa-3x d-flex justify-content-center" />
                      </div>
                      <p>Drop files here to upload.</p>
                      <p><small>Valid High Definition Image</small></p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="col-12 buyer-info-data">
                <div className="d-flex justify-content-start align-items-center"
                     style={{ borderBottom: '0px solid #95AEC7', paddingTop: '20px' }}>
                  <input className="form-check-input" type="checkbox" name="isDeliveryInvoiced"
                         style={{ marginRight: '25px' }} id="isLive"
                         checked={isLive} onChange={(e) => setIsLive(e.target.checked)} />
                  <div className="d-flex justify-content-start flex-fill align-items-center">
                    <div>Is live</div>
                  </div>
                </div>
              </div>

              <div className="col-12 d-flex justify-content-center add-participant-btn-section">
                <button className="btn save-btn" style={{ marginRight: '20px' }} type="submit">Save</button>
                <button className="btn cancel-btn supplier-order-status-change-modal-close"
                        style={{ marginRight: '20px' }} type="button" onClick={onCancel}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </section>
  );
}
