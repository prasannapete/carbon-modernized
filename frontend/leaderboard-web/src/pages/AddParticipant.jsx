import { useCallback, useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import { postForm } from '../services/api';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import Footer from '../components/Footer';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import useBootstrapModal from '../lib/useBootstrapModal';

const PAGE_LENGTH = 25;

// hh:mm:sss validation, identical to add-participant.js validateTime().
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

export default function AddParticipant() {
  const { id: routeId } = useParams();
  const id = routeId || '';

  const [records, setRecords] = useState([]);
  const [sortField, setSortField] = useState('creationTime');
  const [sortDirection, setSortDirection] = useState('asc');
  const [noRecords, setNoRecords] = useState(false);
  const [timeErrors, setTimeErrors] = useState({});
  const [activeSort, setActiveSort] = useState({ id: null, order: null });

  const [modalOpen, setModalOpen] = useState(false);
  const [showReset, setShowReset] = useState(true);
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [nameError, setNameError] = useState('');
  const participantIdRef = useRef(null);

  const [deleteState, setDeleteState] = useState({ open: false, participantId: null, scoreId: null });
  const timeoutRef = useRef(null);

  const modalRef = useBootstrapModal(modalOpen, () => setModalOpen(false));
  const recordsRef = useRef(records);
  recordsRef.current = records;

  const scoreTypeIsPoints = records.length > 0 ? records[0].eventScoreType == 0 : true;
  const scoreHeaderText = records.length > 0 ? (records[0].eventScoreType == 0 ? 'Points' : 'Time') : 'Points';

  const getAllParticipants = useCallback((sField, sDir) => {
    const useField = sField ?? sortField;
    const useDir = sDir ?? sortDirection;
    postForm('/event-participants/get-event-participants-by-event-id', {
      start: 0,
      length: PAGE_LENGTH,
      'order[0][column]': useField,
      'order[0][dir]': useDir,
      sort: useField,
      eventId: id,
    })
      .then((response) => {
        if (response && response.success) {
          const data = (response.data || []).map((val) => {
            if (val.eventScoreType == 1 && val.eventTime == null) return { ...val, eventTime: '00:00:000' };
            return { ...val };
          });
          setRecords(data);
          setNoRecords(data.length < 1);
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, [id, sortField, sortDirection]);

  useEffect(() => {
    getAllParticipants();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const onSortName = (order) => {
    setActiveSort({ id: 'name', order });
    setSortField('name');
    setSortDirection(order);
    getAllParticipants('name', order);
  };

  const onSortScore = (order) => {
    const field = scoreTypeIsPoints ? 'eventScores.score' : 'eventScores.time';
    setActiveSort({ id: 'score', order });
    setSortField(field);
    setSortDirection(order);
    getAllParticipants(field, order);
  };

  const resetForm = () => { setName(''); setAge(''); setNameError(''); };

  const openAdd = () => {
    participantIdRef.current = null;
    setShowReset(true);
    resetForm();
    setModalOpen(true);
  };

  const openEdit = (pid) => {
    participantIdRef.current = pid;
    setShowReset(false);
    postForm('/event-participants/get', { id: pid })
      .then((data) => {
        if (data && data.success === true) {
          setName(data.data.name || '');
          setAge(data.data.age != null ? data.data.age : '');
          setModalOpen(true);
        } else {
          Swal.fire({ title: 'Something went wrong', text: data && data.error, confirmButtonText: CONFIRM_BTN });
        }
      })
      .catch((response) => {
        Swal.fire({ title: 'Something went wrong', text: response && response.d, confirmButtonText: CONFIRM_BTN });
      });
  };

  const saveParticipant = (e) => {
    e.preventDefault();
    if (!name.trim()) { setNameError('Please enter name'); return; }
    setModalOpen(false);
    postForm('/event-participants/save', { name: name.trim(), eventId: id, id: participantIdRef.current })
      .then((data) => {
        if (data && data.success) {
          participantIdRef.current = null;
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
    // Controlled edit of the row's score/time field.
    setRecords((prev) => prev.map((val) => {
      if (val.id !== record.id) return val;
      return scoreTypeIsPoints ? { ...val, eventScores: value } : { ...val, eventTime: value };
    }));

    const scoreId = record.eventScoreId ?? null;
    const formdata = { id: scoreId, participantId: record.id, eventId: id };
    if (scoreTypeIsPoints) formdata.score = value;
    else formdata.time = value;

    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      if (scoreTypeIsPoints) {
        updateScore(formdata);
      } else if (validateTime(value)) {
        setTimeErrors((prev) => ({ ...prev, [record.id]: false }));
        updateScore(formdata);
      } else {
        setTimeErrors((prev) => ({ ...prev, [record.id]: true }));
      }
    }, 1000);
  };

  const confirmDelete = () => {
    const { participantId, scoreId } = deleteState;
    setDeleteState({ open: false, participantId: null, scoreId: null });
    postForm('/event-participants/trash', { id: participantId })
      .then((data) => {
        if (data && data.success === true) {
          Swal.fire({
            title: 'Success', text: 'Participant deleted successfully', allowOutsideClick: false,
            confirmButtonText: CONFIRM_BTN, timer: 1500,
          });
          getAllParticipants();
        } else {
          Swal.fire({ title: 'Something went wrong', text: data && data.error, confirmButtonText: CONFIRM_BTN });
        }
      })
      .catch((response) => {
        Swal.fire({ title: 'Something went wrong', text: response && response.d, confirmButtonText: CONFIRM_BTN });
      });
    if (scoreId != null) {
      postForm('/event-scores/trash', { id: scoreId }).catch(() => {});
    }
  };

  return (
    <section>
      <div className="full-container">
        <div className="main-container row">
          <input name="id" type="hidden" id="id" value={id} readOnly />
          <div className="card-horizontal event-lists-header row">
            <div className="d-flex">
              <div className="col-4">
                <div className="list-selection" id="btn-participant-name">
                  <div aria-expanded="false" aria-haspopup="true"
                       className="btn dropdown-toggle event-lists-dropdowns" data-bs-toggle="dropdown" role="button">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> Name</span>
                      <span className="ti-angle-down ml-4" />
                    </div>
                  </div>
                  <div className="dropdown-menu dropdown-menu-left header-content-dropdown">
                    <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === 'name' && activeSort.order === 'asc' ? ' active' : ''}`}
                       data-field="name" data-order="asc" href="#" onClick={(e) => { e.preventDefault(); onSortName('asc'); }}>
                      <div className="dropdown-items-icon"><img src="/images/icons/a-to-z-gray.svg" /></div>
                      <div className="select-dropdown-items-options">Ascending</div>
                    </a>
                    <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === 'name' && activeSort.order === 'desc' ? ' active' : ''}`}
                       data-order="desc" href="#" onClick={(e) => { e.preventDefault(); onSortName('desc'); }}>
                      <div className="dropdown-items-icon"><img src="/images/icons/z-to-a.svg" /></div>
                      <div className="select-dropdown-items-options"> Descending</div>
                    </a>
                  </div>
                </div>
              </div>
              <div className="col-4">
                <div className="list-selection" id="btn-participant-score">
                  <div aria-expanded="false" aria-haspopup="true"
                       className="btn dropdown-toggle event-lists-dropdowns" data-bs-toggle="dropdown" role="button">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns score-type-text"> {scoreHeaderText}</span>
                      <span className="ti-angle-down ml-4" />
                    </div>
                  </div>
                  <div className="dropdown-menu dropdown-menu-left header-content-dropdown">
                    <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === 'score' && activeSort.order === 'asc' ? ' active' : ''}`}
                       data-field="score" data-order="asc" href="#" onClick={(e) => { e.preventDefault(); onSortScore('asc'); }}>
                      <div className="dropdown-items-icon"><img src="/images/icons/a-to-z-gray.svg" /></div>
                      <div className="select-dropdown-items-options">Ascending</div>
                    </a>
                    <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === 'score' && activeSort.order === 'desc' ? ' active' : ''}`}
                       data-order="desc" href="#" onClick={(e) => { e.preventDefault(); onSortScore('desc'); }}>
                      <div className="dropdown-items-icon"><img src="/images/icons/z-to-a.svg" /></div>
                      <div className="select-dropdown-items-options"> Descending</div>
                    </a>
                  </div>
                </div>
              </div>
              <div className="col-2">
                <div className="list-selection" id="is-live2">
                  <div aria-expanded="false" aria-haspopup="true"
                       className="btn dropdown-toggle event-lists-dropdowns" data-bs-toggle="dropdown" role="button">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="col-12">
          {noRecords && (
            <div className="no-records-found">
              <div className="d-flex align-items-center">
                <div className="w-100"><h5 className="text-center" style={{ marginTop: '20px' }}>No records found!</h5></div>
              </div>
            </div>
          )}
        </div>

        <div className="list-item-single row" id="list-records">
          {records.map((record) => (
            <div className="card-horizontal my-orders-header-output column mb-4" data-record-id={record.id} key={record.id}>
              <div className="d-flex align-items-center">
                <div className="col-4 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {record.name}</span>
                    </div>
                  </div>
                </div>
                <div className="col-4 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content purchase-analytics-value" style={{ width: '250px' }}>
                      <input className="edit-input-field score-input"
                             value={record.eventScoreType == 0 ? (record.eventScores ?? '') : (record.eventTime ?? '')}
                             onChange={(e) => onScoreInput(record, e.target.value)} />
                    </div>
                  </div>
                  <div className="append-validation-error justify-content-start align-items-center field-validation-error"
                       style={{ textAlign: 'right', display: timeErrors[record.id] ? 'flex' : 'none', fontSize: '20px' }}>
                    Please enter valid time of format hh:mm:sss
                  </div>
                </div>
                <div className="col-4 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-edit-section">
                      <span className="current-selection current-selection-columns edit-participants" onClick={() => openEdit(record.id)}>
                        <img className="edit-icon" style={{ fontSize: '47px' }} src="/images/icons/writing.svg" /></span>
                      <span className="current-selection current-selection-columns delete-participant"
                            onClick={() => setDeleteState({ open: true, participantId: record.id, scoreId: record.eventScoreId ?? null })}>
                        <img className="delete-icon" src="/images/icons/delete-black.svg" /></span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
          <div className="d-flex justify-content-center">
            <div className="items-load-more" style={{ cursor: 'pointer', marginRight: '10px', display: 'none' }}>
              <img src="/images/icons/horizontal-options-icon.svg" width="50px" height="50px" />
            </div>
            <button type="button" className="btn btn-add-user" id="btn-create-new-participants" onClick={openAdd}>
              <img src="/images/icons/add-white-icon.svg" height="25px" width="25px" className="add-user-icon" />&nbsp;
              <span className="add-user" style={{ textTransform: 'none' }}>Add participants</span>
            </button>
          </div>
        </div>
      </div>

      <Footer />

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
                    <button className="btn reset-btn supplier-order-status-change-modal-close" type="button"
                            style={{ display: showReset ? '' : 'none' }} onClick={resetForm}>Reset</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <DeleteConfirmModal open={deleteState.open} onConfirm={confirmDelete}
                          onClose={() => setDeleteState({ open: false, participantId: null, scoreId: null })} />
    </section>
  );
}
