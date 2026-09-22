import { useCallback, useEffect, useRef, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { postForm } from '../services/api';
import { useApp } from '../context/AppContext';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import Footer from '../components/Footer';

// Mirrors event-display-score.html + event-display-score.js.
export default function EventDisplayScore() {
  const { id: routeId } = useParams();
  const [searchParams] = useSearchParams();
  const { registerLeadPagination } = useApp();

  const selectedEventIdRef = useRef(searchParams.get('id') || routeId || null);
  const sortFieldRef = useRef('score');
  const sortDirectionRef = useRef('asc');
  const pageLengthRef = useRef(10);
  const scoreTypeRef = useRef(0);

  const [records, setRecords] = useState([]);
  const [noRecords, setNoRecords] = useState(false);
  const [scoreType, setScoreType] = useState(0);
  const [eventName, setEventName] = useState('');

  const applyBackground = (color, logo) => {
    const html = document.documentElement;
    if (logo != null && logo !== '') {
      html.style.backgroundImage = `url('${logo}')`;
      html.style.backgroundSize = 'cover';
      html.style.backgroundRepeat = 'no-repeat';
      html.style.backgroundPosition = 'center center';
      html.style.backgroundColor = '';
      document.body.style.background = 'transparent';
    } else {
      html.style.backgroundImage = 'none';
      html.style.backgroundColor = color || '';
      document.body.style.background = 'transparent';
    }
    if (color) html.style.backgroundColor = color;
  };

  const fetchScores = useCallback(() => {
    postForm('/event-scores/get-event-score-by-event-id', {
      start: 0,
      length: pageLengthRef.current,
      'order[0][column]': sortFieldRef.current,
      'order[0][dir]': sortDirectionRef.current,
      sort: sortFieldRef.current,
      eventId: selectedEventIdRef.current,
    })
      .then((response) => {
        if (response && response.success) {
          const data = response.data || [];
          setRecords(data);
          setNoRecords(data.length < 1);
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, []);

  const getAllEvents = useCallback(() => {
    return postForm('/events/get-all', { start: 0, length: 10 })
      .then((response) => {
        if (response && response.success) {
          (response.data || []).forEach((val) => {
            if (selectedEventIdRef.current == val.id) {
              selectedEventIdRef.current = val.id;
              scoreTypeRef.current = val.scoreType;
              setScoreType(val.scoreType);
              setEventName(val.eventName);
              if (val.scoreType == 0) {
                sortFieldRef.current = 'score';
                sortDirectionRef.current = 'desc';
              } else {
                sortFieldRef.current = 'time';
                sortDirectionRef.current = 'asc';
              }
              applyBackground(val.primaryColor, val.logoPath);
            }
          });
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, []);

  useEffect(() => {
    getAllEvents().then(() => fetchScores());
    // Auto-refresh the leaderboard every 20s (the reference intent).
    const timer = setInterval(() => fetchScores(), 20000);
    return () => {
      clearInterval(timer);
      // Reset the page-level background so other pages are unaffected.
      document.documentElement.style.backgroundImage = 'none';
      document.body.style.background = '';
    };
  }, [getAllEvents, fetchScores]);

  // Header 10/20/30 buttons set the page size and reload.
  useEffect(() => {
    registerLeadPagination((size) => { pageLengthRef.current = size; fetchScores(); });
    return () => registerLeadPagination(null);
  }, [registerLeadPagination, fetchScores]);

  return (
    <>
      <div className="col-12 select-event-contents">
        <input name="eventId" type="hidden" id="eventId" value={selectedEventIdRef.current || ''} readOnly />
      </div>

      <div className="data-section scrollable-content-with-title-and-filter">
        <div className="main-container row">
          <div className="d-flex justify-content-end col-10 display-pagination-section" />
          <div className="card-horizontal event-lists-header row resizable">
            <div className="d-flex justify-content-between">
              <div className="col-10 participant-name-col">
                <div className="list-selection" id="btn-participant-name">
                  <div className="btn-current-selection event-lists-header-content">
                    <span className="current-selection current-selection-columns"> Participant Name</span>
                  </div>
                </div>
              </div>
              <div className="col-2 score-section" style={{ paddingLeft: '20px' }}>
                <div className="list-selection" id="btn-score">
                  <div className="btn-current-selection event-lists-header-content">
                    <span className="current-selection current-selection-columns in-score"
                          style={{ display: scoreType == 0 ? '' : 'none' }}> Points</span>
                    <span className="in-time" style={{ display: scoreType == 0 ? 'none' : '' }}>Time</span>
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

        <div className="list-item-single row" id="list-event-score-records">
          {records.map((record) => (
            <div className="card-horizontal my-orders-header-output column mb-4" data-record-id={record.id} key={record.id}>
              <div className="d-flex align-items-center">
                <div className="col-10 event-lists-output participant-name-col">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {record.participantName}</span>
                    </div>
                  </div>
                </div>
                <div className="col-2 event-lists-output score-section">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> {record.eventScoreType == 0 ? record.score : record.time}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="display-name-section d-flex justify-content-center align-items-center">
          <div className="col-8">
            <div className="event-name-section">
              <span className="event-name-display">{eventName}</span>
            </div>
          </div>
        </div>
      </div>

      <Footer />
    </>
  );
}
