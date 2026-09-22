import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';
import { postForm } from '../services/api';
import { useApp } from '../context/AppContext';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import Footer from '../components/Footer';

const PAGE_LENGTH = 25;
const ALERT_DISPLAY_TIME = 1500;
const GLOBAL_DATE_FORMAT = 'YYYY-MM-DD';

const SORT_COLUMNS = [
  { id: 'btn-event-name', label: 'Event name', field: 'eventName' },
  { id: 'btn-start-date', label: 'Start date', field: 'startDate' },
  { id: 'btn-end-date', label: 'End date', field: 'endDate' },
  { id: 'is-live', label: 'Is live', field: 'isLive' },
];

function formatDate(value) {
  return value ? moment(String(value)).format(GLOBAL_DATE_FORMAT) : '';
}

export default function EventList() {
  const navigate = useNavigate();
  const { registerLeadPagination } = useApp();

  const [records, setRecords] = useState([]);
  const [recordsFiltered, setRecordsFiltered] = useState(0);
  const [loading, setLoading] = useState(false);
  const [noRecords, setNoRecords] = useState(false);
  const [activeSort, setActiveSort] = useState({ id: null, order: null });
  const [openSort, setOpenSort] = useState(null); // which column's sort dropdown is open
  const [deleteState, setDeleteState] = useState({ open: false, eventId: null });

  const sortHeaderRef = useRef(null);
  const startFromRef = useRef(0);

  // Close the column sort dropdown when clicking outside the header row.
  useEffect(() => {
    const onDocClick = (e) => {
      if (sortHeaderRef.current && !sortHeaderRef.current.contains(e.target)) setOpenSort(null);
    };
    document.addEventListener('click', onDocClick);
    return () => document.removeEventListener('click', onDocClick);
  }, []);
  const excelPageLengthRef = useRef(30); // default excelPageLength; header 10/20/30 buttons change it

  // Header "Events" dropdown 10/20/30 buttons -> leadPagination(size) sets excel length.
  useEffect(() => {
    registerLeadPagination((size) => { excelPageLengthRef.current = size; });
    return () => registerLeadPagination(null);
  }, [registerLeadPagination]);

  // Preserves the reference semantics: sort/order are sent empty unless a sort
  // dropdown was clicked (jQuery serialized the undefined params as empty strings).
  const getPagedEvents = useCallback((resetList, from, sField, sDir) => {
    let start = from;
    if (resetList) {
      start = 0;
      startFromRef.current = 0;
      setRecords([]);
    }
    setLoading(true);
    setNoRecords(false);
    postForm('/events/get-events', {
      start,
      length: PAGE_LENGTH,
      'order[0][column]': sField ?? '',
      'order[0][dir]': sDir ?? '',
      sort: sField ?? '',
      'search[value]': '',
      'search[regex]': false,
    })
      .then((response) => {
        setLoading(false);
        if (response && response.success) {
          setRecordsFiltered(response.recordsFiltered);
          const incoming = (response.data && response.data.data) || [];
          setRecords((prev) => {
            const next = resetList ? [...incoming] : [...prev, ...incoming];
            setNoRecords(next.length < 1);
            return next;
          });
        }
      })
      .catch((data) => {
        setLoading(false);
        Swal.fire({
          title: 'Something went wrong',
          confirmButtonText: CONFIRM_BTN,
          text: data && data.d,
          timer: ALERT_DISPLAY_TIME,
        });
      });
  }, []);

  useEffect(() => {
    getPagedEvents(false, 0);
  }, [getPagedEvents]);

  const onSort = (col, order) => {
    setActiveSort({ id: col.id, order });
    setOpenSort(null);
    getPagedEvents(true, 0, col.field, order);
  };

  const onLoadMore = () => {
    if (records.length < recordsFiltered) {
      startFromRef.current += PAGE_LENGTH;
      getPagedEvents(false, startFromRef.current);
    }
  };

  const downloadExcel = (record) => {
    const scoreType = record.scoreType;
    let excelSort = 'score';
    let excelSortDir = 'desc';
    if (scoreType == 0) {
      excelSort = 'score';
      excelSortDir = 'desc';
    } else {
      excelSort = 'time';
      excelSortDir = 'asc';
    }
    postForm('/event-scores/export-score-to-excel', {
      eventId: record.id,
      order: excelSortDir,
      sort: excelSort,
      scoreType,
      length: excelPageLengthRef.current,
    })
      .then((response) => {
        if (response && response.success) {
          window.location.href = response.excelUploadPath;
        }
      })
      .catch((data) => {
        Swal.fire({
          confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px">',
          title: 'Something went wrong!',
          text: data && data.d,
          timer: 1500,
        });
      });
  };

  const confirmDelete = () => {
    const id = deleteState.eventId;
    setDeleteState({ open: false, eventId: null });
    postForm('/events/trash', { id })
      .then((data) => {
        if (data && data.success === true) {
          Swal.fire({
            title: 'Success',
            text: 'Event deleted successfully',
            confirmButtonText: CONFIRM_BTN,
            allowOutsideClick: false,
            timer: 1500,
          });
          getPagedEvents(true, 0);
        } else {
          Swal.fire({ title: 'Something went wrong', text: data && data.error, confirmButtonText: CONFIRM_BTN });
        }
      })
      .catch((response) => {
        Swal.fire({ title: 'Something went wrong', text: response && response.d, confirmButtonText: CONFIRM_BTN });
      });
  };

  const showLoadMore = records.length < recordsFiltered && records.length > 0;

  return (
    <>
      <div className="full-container">
        <div className="main-container row">
          <div className="col-12">
            {noRecords && (
              <div className="no-records-found">
                <div className="d-flex align-items-center">
                  <div className="w-100">
                    <h5 className="text-center" style={{ marginTop: '20px' }}>No records found!</h5>
                  </div>
                </div>
              </div>
            )}
          </div>
          <div className="card-horizontal event-lists-header row" ref={sortHeaderRef}>
            <div className="d-flex">
              {SORT_COLUMNS.map((col) => (
                <div className="col-2" key={col.id}>
                  <div className={`list-selection${openSort === col.id ? ' show' : ''}`} id={col.id}>
                    <div aria-expanded={openSort === col.id} aria-haspopup="true"
                         className={`btn dropdown-toggle event-lists-dropdowns${openSort === col.id ? ' show' : ''}`} role="button"
                         onClick={(e) => { e.stopPropagation(); setOpenSort((cur) => (cur === col.id ? null : col.id)); }}>
                      <div className="btn-current-selection event-lists-header-content">
                        <span className="current-selection current-selection-columns"> {col.label}</span>
                        <span className="ti-angle-down ml-4" />
                      </div>
                    </div>
                    <div className={`dropdown-menu dropdown-menu-left header-content-dropdown${openSort === col.id ? ' show' : ''}`}>
                      <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === col.id && activeSort.order === 'asc' ? ' active' : ''}`}
                         data-field={col.field} data-order="asc" href="#"
                         onClick={(e) => { e.preventDefault(); onSort(col, 'asc'); }}>
                        <div className="dropdown-items-icon"><img src="/images/icons/a-to-z-gray.svg" /></div>
                        <div className="select-dropdown-items-options">Ascending</div>
                      </a>
                      <a className={`dropdown-item header-content-dropdown-items current-selection${activeSort.id === col.id && activeSort.order === 'desc' ? ' active' : ''}`}
                         data-order="desc" href="#"
                         onClick={(e) => { e.preventDefault(); onSort(col, 'desc'); }}>
                        <div className="dropdown-items-icon"><img src="/images/icons/z-to-a.svg" /></div>
                        <div className="select-dropdown-items-options"> Descending</div>
                      </a>
                    </div>
                  </div>
                </div>
              ))}
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
          {loading && (
            <div className="loading-records">
              <div className="d-flex align-items-center">
                <div className="w-100">
                  <div className="text-center mb-5"><i className="fa fa-spinner fa-pulse fa-5x fa-fw" /></div>
                  <div><h6 className="text-center">Loading</h6></div>
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="list-item-single row" id="list-records">
          {records.map((record) => (
            <div className="card-horizontal my-orders-header-output column mb-4" data-record-id={record.id}
                 key={record.id} style={{ cursor: 'default' }}>
              <div className="d-flex align-items-center">
                <div className="col-2 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {record.eventName}</span>
                    </div>
                  </div>
                </div>
                <div className="col-2 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {formatDate(record.startDate)}</span>
                    </div>
                  </div>
                </div>
                <div className="col-2 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {formatDate(record.endDate)}</span>
                    </div>
                  </div>
                </div>
                <div className="col-2 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns">{record.isLive == 1 ? 'Yes' : 'No'}</span>
                    </div>
                  </div>
                </div>
                <div className="col-2 event-lists-output">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-edit-section">
                      <span className="current-selection current-selection-columns btn-add-participant d-flex justify-content-center align-items-center"
                            onClick={() => navigate(`/event-participants/new-participant/${record.id}`)}>
                        <img className="add-participant-icon" src="/images/icons/add-user.svg" /></span>
                      <span className="current-selection current-selection-columns btn-update-score d-flex justify-content-center align-items-center"
                            onClick={() => navigate(`/events/update-score/${record.id}`)}>
                        <img className="update-icon" style={{ fontSize: '47px' }} src="/images/icons/update.svg" /></span>
                      <span className="current-selection current-selection-columns edit-event d-flex justify-content-center align-items-center"
                            onClick={() => navigate(`/events/create-event/${record.id}`)}>
                        <img className="edit-icon" style={{ fontSize: '47px' }} src="/images/icons/writing.svg" /></span>
                      <span className="current-selection current-selection-columns delete-event d-flex justify-content-center align-items-center"
                            onClick={() => setDeleteState({ open: true, eventId: record.id })}>
                        <img className="delete-icon" style={{ fontSize: '47px' }} src="/images/icons/delete-black.svg" /></span>
                      <span type="button" className="btn btn-add-user btn-download-excel d-flex justify-content-center align-items-center"
                            style={{ cursor: 'pointer' }} onClick={() => downloadExcel(record)}>
                        <img src="/images/logos/download-pdf.svg" height="25px" width="25px" className="add-user-icon" />&nbsp;
                        <span className="add-user" style={{ textTransform: 'none' }}>Excel</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
          <div className="d-flex justify-content-center">
            <div className="items-load-more" style={{ cursor: 'pointer', marginRight: '10px', display: showLoadMore ? '' : 'none' }}
                 onClick={onLoadMore}>
              <img src="/images/icons/horizontal-options-icon.svg" width="50px" height="50px" />
            </div>
            <button type="button" className="btn btn-add-user" id="btn-create-new-event"
                    onClick={() => navigate('/events/create-event')}>
              <img src="/images/icons/add-white-icon.svg" height="25px" width="25px" className="add-user-icon" />&nbsp;
              <span className="add-user" style={{ textTransform: 'none' }}>Add events</span>
            </button>
          </div>
        </div>
      </div>

      <Footer />
      <DeleteConfirmModal open={deleteState.open} onConfirm={confirmDelete}
                          onClose={() => setDeleteState({ open: false, eventId: null })} />
    </>
  );
}
