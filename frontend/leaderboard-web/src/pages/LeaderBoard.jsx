import { useCallback, useEffect, useRef, useState } from 'react';
import { postForm } from '../services/api';
import { useApp } from '../context/AppContext';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import LeaderCarousel, { carouselItemWidth } from '../components/LeaderCarousel';

// Mirrors lead-board/leader-board.html + leader-board.js (+ ce-leaderboard-contents.html).
export default function LeaderBoard() {
  const { registerLeadPagination } = useApp();
  const [records, setRecords] = useState([]);
  const pageLengthRef = useRef(10);
  const recordsRef = useRef(records);
  recordsRef.current = records;
  const [dims] = useState(() => carouselItemWidth(window.innerWidth));

  const getAllLeaderBoardEvents = useCallback(() => {
    postForm('/events/get-leader-board-events', { start: 0, length: pageLengthRef.current })
      .then((response) => {
        if (response && response.success) {
          const incoming = response.data || [];
          setRecords((prev) => {
            if (prev.length <= 0) return incoming;
            // Merge fresh scores into existing event cards (no full re-render/reset).
            return prev.map((eventVal) => {
              const match = incoming.find((val) => val.id == eventVal.id);
              return match ? { ...eventVal, eventScoresDTOList: match.eventScoresDTOList } : eventVal;
            });
          });
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, []);

  useEffect(() => {
    // Hide the shared header logo on the leaderboard, restore on leave.
    const logo = document.querySelector('.main-logo-icon');
    const prevDisplay = logo ? logo.style.display : null;
    if (logo) logo.style.display = 'none';

    getAllLeaderBoardEvents();
    const timer = setInterval(() => getAllLeaderBoardEvents(), 20000);
    return () => {
      clearInterval(timer);
      if (logo) logo.style.display = prevDisplay || '';
    };
  }, [getAllLeaderBoardEvents]);

  useEffect(() => {
    registerLeadPagination((size) => { pageLengthRef.current = size; getAllLeaderBoardEvents(); });
    return () => registerLeadPagination(null);
  }, [registerLeadPagination, getAllLeaderBoardEvents]);

  const renderCard = (record) => (
    <div className="leader-board-event-list" data-record-id={record.id} key={record.id}
         style={{
           backgroundColor: record.primaryColor,
           backgroundImage: `url(${record.logoPath})`,
           backgroundSize: 'cover',
           backgroundPosition: 'center',
           backgroundRepeat: 'no-repeat',
         }}>
      <div className="leader-board-events" data-id={record.id}>
        <div className="data-section">
          <div className="main-container row">
            <div className="card-horizontal event-lists-header row resizable">
              <div className="d-flex justify-content-between">
                <div className="col-9 leaderboard-name-col">
                  <div className="list-selection" id="btn-participant-name">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns"> Participant Name</span>
                    </div>
                  </div>
                </div>
                <div className="col-3 leaderboard-score-column" style={{ paddingLeft: '20px' }}>
                  <div className="list-selection" id="btn-score">
                    <div className="btn-current-selection event-lists-header-content">
                      {record.scoreType == 0
                        ? <span className="current-selection current-selection-columns in-score"> Points</span>
                        : <span className="in-time">Time</span>}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="col-12">
            {(record.eventScoresDTOList || []).length <= 0 && (
              <div className="no-records-found">
                <div className="d-flex align-items-center">
                  <div className="w-100"><h5 className="text-center" style={{ marginTop: '20px' }}>No records found!</h5></div>
                </div>
              </div>
            )}
          </div>
          <div className="list-item-single row" id="list-event-score-records">
            {(record.eventScoresDTOList || []).map((eventScore) => (
              <div className="card-horizontal my-orders-header-output column mb-4" data-record-id={eventScore.id} key={eventScore.id}>
                <div className="d-flex align-items-center">
                  <div className="col-9 event-lists-output leaderboard-name-col">
                    <div className="list-selection">
                      <div className="btn-current-selection event-lists-header-content">
                        <span className="current-selection current-selection-columns"> {eventScore.participantName}</span>
                      </div>
                    </div>
                  </div>
                  <div className="col-3 event-lists-output leaderboard-score-column">
                    <div className="list-selection">
                      <div className="btn-current-selection event-lists-header-content">
                        <span className="current-selection current-selection-columns"> {eventScore.eventScoreType == 0 ? eventScore.score : eventScore.time}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
        <div className="display-name-section d-flex justify-content-center align-items-center">
          <div className="col-8">
            <div className="event-name-section">
              <span className="event-name-display">{record.eventName}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <LeaderCarousel id="leadboard-list" itemWidth={dims.itemWidth} gap={dims.gap}>
      {records.map(renderCard)}
    </LeaderCarousel>
  );
}
