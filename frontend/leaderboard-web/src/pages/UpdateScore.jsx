import { useCallback, useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import { postForm } from '../services/api';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import Footer from '../components/Footer';
import useBootstrapModal from '../lib/useBootstrapModal';

const PAGE_LENGTH = 25;

function validateTime(inputTime) {
  const regex = /^(?:[01]\d|2[0-3]):(?:[0-5]\d):(?:[0-9]\d)\d{1,3}$/;
  if (regex.test(inputTime)) {
    const parts = inputTime.split(':');
    const hours = parseInt(parts[0], 10);
    const minutes = parseInt(parts[1], 10);
    const secondsAndMilliseconds = parts[2];
    if (hours < 24 && minutes < 60 && secondsAndMilliseconds < 1000) return true;
  }
  return false;
}

export default function UpdateScore() {
  const { eventId: routeEventId } = useParams();
  const id = routeEventId || '';

  const [records, setRecords] = useState([]);
  const [scoreType, setScoreType] = useState(0);
  const [noRecords, setNoRecords] = useState(false);
  const [timeErrors, setTimeErrors] = useState({});

  const [modalOpen, setModalOpen] = useState(false);
  const [name, setName] = useState('');
  const [nameError, setNameError] = useState('');
  const timeoutRef = useRef(null);
  const modalRef = useBootstrapModal(modalOpen, () => setModalOpen(false));

  const getAllParticipants = useCallback(() => {
    postForm('/event-participants/get-event-participants-by-event-id', {
      start: 0,
      length: PAGE_LENGTH,
      'order[0][column]': 'creationTime',
      'order[0][dir]': 'asc',
      sort: 'creationTime',
      eventId: id,
    })
      .then((response) => {
        if (response && response.success) {
          const data = response.data || [];
          if (data.length > 0) {
            const st = data[0].eventScoreType;
            const mapped = data.map((val) => {
              if (st == 1 && val.eventTime == null) return { ...val, eventTime: '00:00:000' };
              return { ...val };
            });
            setRecords(mapped);
            setScoreType(st);
            setNoRecords(mapped.length < 1);
          } else {
            setRecords([]);
            setNoRecords(true);
          }
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, [id]);

  useEffect(() => { getAllParticipants(); }, [getAllParticipants]);

  const updateScore = (formdata) => {
    postForm('/event-scores/save', formdata)
      .then((data) => {
        if (data && data.success) {
          setRecords((prev) => prev.map((val) => {
            if (val.id == data.eventScoresDTO.participantId) {
              return { ...val, eventScoreId: data.eventScoresDTO.id, eventScores: data.eventScoresDTO.score, eventTime: data.eventScoresDTO.time };
            }
            return val;
          }));
        } else {
          Swal.fire({ title: 'Something went wrong', text: data && data.error, confirmButtonText: CONFIRM_BTN });
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', text: data && data.d, confirmButtonText: CONFIRM_BTN });
      });
  };

  const onScoreInput = (record, value) => {
    setRecords((prev) => prev.map((val) => {
      if (val.id !== record.id) return val;
      return scoreType == 0 ? { ...val, eventScores: value } : { ...val, eventTime: value };
    }));

    const scoreId = record.eventScoreId ?? null;
    const formdata = { id: scoreId, participantId: record.id, eventId: id };
    if (scoreType == 0) formdata.score = value;
    else formdata.time = value;

    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      if (scoreType == 0) {
        updateScore(formdata);
      } else if (validateTime(value)) {
        setTimeErrors((prev) => ({ ...prev, [record.id]: false }));
        updateScore(formdata);
      } else {
        setTimeErrors((prev) => ({ ...prev, [record.id]: true }));
      }
    }, 1000);
  };

  const resetForm = () => { setName(''); setNameError(''); };

  const saveParticipant = (e) => {
    e.preventDefault();
    if (!name.trim()) { setNameError('Please enter name'); return; }
    setModalOpen(false);
    postForm('/event-participants/save', { name: name.trim(), eventId: id })
      .then((data) => {
        if (data && data.success) {
          Swal.fire({
            title: 'Success', text: 'Saved successfully', allowOutsideClick: false,
            confirmButtonText: CONFIRM_BTN, timer: 1500,
          });
          getAllParticipants();
          resetForm();
        } else {
          Swal.fire({ title: 'Something went wrong', text: data && data.error, confirmButtonText: CONFIRM_BTN });
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  };

  return (
    <section>
      <div className="main-container">
        <input name="eventId" type="hidden" id="eventId" value={id} readOnly />
        <div className="update-score-container" id="update-score-list">
          <div className="row d-flex justify-content-center">
            <div className="col-md-10">
              <div className="update-score-header-section">
                <div className="col-md-12 d-flex">
                  <div className="col-8 update-score-title">Participant name</div>
                  <div className="col-4 update-score-title" style={{ marginLeft: '20px' }}>
                    {scoreType == 0 ? 'Points' : 'Time'}
                  </div>
                </div>
              </div>
              <div className="col-12">
                {noRecords && (
                  <div className="no-records-found" style={{ marginBottom: '20px' }}>
                    <div className="d-flex align-items-center">
                      <div className="w-100"><h5 className="text-center" style={{ marginTop: '20px' }}>No records found!</h5></div>
                    </div>
                  </div>
                )}
              </div>
              {records.map((record) => (
                <div className="row" data-record-id={record.id} key={record.id}>
                  <div className="col-md-12">
                    <div className="row">
                      <div className="col-12 participant-score-data">
                        <div className="article-card-expanded-spec-container-buyer-info">
                          <div className="d-flex justify-content-start align-items-center">
                            <div className="purchase-analytics-title">{record.name}</div>
                            <div className="d-flex justify-content-end align-items-end flex-fill flex-column">
                              <div className="flex-column">
                                <div className="purchase-analytics-value time-field d-flex">
                                  <input className="edit-input-field score-input"
                                         value={record.eventScoreType == 0 ? (record.eventScores ?? '') : (record.eventTime ?? '')}
                                         onChange={(e) => onScoreInput(record, e.target.value)} />
                                </div>
                              </div>
                              <div className="append-validation-error justify-content-end align-items-center field-validation-error"
                                   style={{ textAlign: 'right', display: timeErrors[record.id] ? 'flex' : 'none', fontSize: '20px' }}>
                                Please enter valid time of format hh:mm:sss
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <div className="d-flex justify-content-center">
              <button type="button" className="btn btn-add-user" id="btn-create-new-participants" onClick={() => setModalOpen(true)}>
                <img src="/images/icons/add-white-icon.svg" height="25px" width="25px" className="add-user-icon" />&nbsp;
                <span className="add-user" style={{ textTransform: 'none' }}>Add Participants</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* add-participant-popup */}
      <div className="modal" data-bs-backdrop="static" id="modal-add-participant-pop-up" role="dialog"
           style={{ zIndex: 1061, fontSize: '20px' }} tabIndex={-1} ref={modalRef}>
        <div className="modal-dialog modal-dialog-centered modal-md" role="document">
          <div className="modal-content">
            <div className="modal-body">
              <div className="row" style={{ paddingLeft: '20px', paddingRight: '20px' }}>
                <form className="form-supplier-order-status-change" id="add-participant-form" method="POST" onSubmit={saveParticipant}>
                  <div className="col-12">
                    <div className="purchase-analytics-title form-primary"> Name</div>
                    <div className="d-flex justify-content-start align-items-center add-participant-modal-input-section">
                      <div className="flex-column append-validation-error">
                        <input className="purchase-analytics-value d-flex buyer-info-dropdown-content add-participant-modal-input add-participant-input"
                               id="name" name="name" value={name} onChange={(e) => setName(e.target.value)} />
                        {nameError && <div className="field-validation-error">{nameError}</div>}
                      </div>
                    </div>
                    <div className="append-validation-error d-flex justify-content-end align-items-center" />
                  </div>
                  <div className="col-12 d-flex justify-content-center add-participant-btn-section">
                    <button className="btn save-btn" style={{ marginRight: '20px' }} type="submit">Save</button>
                    <button className="btn cancel-btn supplier-order-status-change-modal-close" style={{ marginRight: '20px' }}
                            type="button" onClick={() => { setModalOpen(false); resetForm(); }}>Cancel</button>
                    <button className="btn reset-btn supplier-order-status-change-modal-close" type="button" onClick={resetForm}>Reset</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <Footer />
    </section>
  );
}
