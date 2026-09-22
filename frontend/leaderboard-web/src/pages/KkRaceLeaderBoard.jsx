import { useCallback, useEffect, useRef, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { postForm } from '../services/api';
import Swal, { CONFIRM_BTN } from '../lib/swal';
import LeaderCarousel, { carouselItemWidth } from '../components/LeaderCarousel';

// Mirrors kk-race-played/leader-board.html + kk-race-played/leader-board.js.
export default function KkRaceLeaderBoard() {
  const { consoleId: routeConsoleId } = useParams();
  const [searchParams] = useSearchParams();
  const selectedConsoleId = routeConsoleId || searchParams.get('consoleId') || '';

  const [records, setRecords] = useState([]);
  const recordsRef = useRef(records);
  recordsRef.current = records;
  const [dims] = useState(() => carouselItemWidth(window.innerWidth));

  const normalize = useCallback((data) => {
    if (Array.isArray(data)) {
      return [{ consoleId: selectedConsoleId, title: `Console ${selectedConsoleId}`, raceInfoList: data }];
    }
    const result = [];
    Object.entries(data || {}).forEach(([consoleId, raceInfoList]) => {
      result.push({ consoleId, title: `Console ${consoleId}`, raceInfoList: raceInfoList || [] });
    });
    return result;
  }, [selectedConsoleId]);

  const getRaceInfoLeaderBoard = useCallback(() => {
    const requestData = {};
    if (selectedConsoleId != null && selectedConsoleId !== '') requestData.consoleId = selectedConsoleId;
    postForm('/kk-race-played/race-info', requestData)
      .then((response) => {
        if (response && response.success) {
          const incoming = normalize(response.data);
          setRecords((prev) => {
            if (selectedConsoleId || prev.length <= 0) return incoming;
            // Merge fresh race lists into existing console cards.
            return prev.map((item) => {
              const match = incoming.find((r) => String(r.consoleId) === String(item.consoleId));
              return match ? { ...item, raceInfoList: match.raceInfoList } : item;
            });
          });
        } else {
          setRecords([]);
        }
      })
      .catch((data) => {
        Swal.fire({ title: 'Something went wrong', confirmButtonText: CONFIRM_BTN, text: data && data.d });
      });
  }, [selectedConsoleId, normalize]);

  useEffect(() => {
    const logo = document.querySelector('.main-logo-icon');
    const prevDisplay = logo ? logo.style.display : null;
    if (logo) logo.style.display = 'none';

    getRaceInfoLeaderBoard();
    const timer = setInterval(getRaceInfoLeaderBoard, 20000);
    return () => {
      clearInterval(timer);
      if (logo) logo.style.display = prevDisplay || '';
    };
  }, [getRaceInfoLeaderBoard]);

  const renderCard = (record) => (
    <div className="leader-board-event-list" data-console-id={record.consoleId} key={record.consoleId}>
      <div className="leader-board-events">
        <div className="data-section">
          <div className="main-container row">
            <div className="card-horizontal event-lists-header row resizable">
              <div className="d-flex justify-content-between">
                <div className="col-9 leaderboard-name-col">
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns">Player Name</span>
                    </div>
                  </div>
                </div>
                <div className="col-3 leaderboard-score-column" style={{ paddingLeft: '20px' }}>
                  <div className="list-selection">
                    <div className="btn-current-selection event-lists-header-content">
                      <span className="current-selection current-selection-columns in-time">Time</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="col-12">
            {(record.raceInfoList || []).length <= 0 && (
              <div className="no-records-found">
                <div className="d-flex align-items-center">
                  <div className="w-100"><h5 className="text-center" style={{ marginTop: '20px' }}>No records found!</h5></div>
                </div>
              </div>
            )}
          </div>
          <div className="list-item-single row">
            {(record.raceInfoList || []).map((raceInfo) => (
              <div className="card-horizontal my-orders-header-output column mb-4" data-record-id={raceInfo.id} key={raceInfo.id}>
                <div className="d-flex align-items-center">
                  <div className="col-9 event-lists-output leaderboard-name-col">
                    <div className="list-selection">
                      <div className="btn-current-selection event-lists-header-content">
                        <span className="current-selection current-selection-columns">{raceInfo.playerName}</span>
                      </div>
                    </div>
                  </div>
                  <div className="col-3 event-lists-output leaderboard-score-column">
                    <div className="list-selection">
                      <div className="btn-current-selection event-lists-header-content">
                        <span className="current-selection current-selection-columns">{raceInfo.raceDurationFormatted}</span>
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
              <span className="event-name-display">{record.title}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <>
      <input name="consoleId" type="hidden" id="consoleId" value={selectedConsoleId} readOnly />
      <LeaderCarousel id="kk-race-leadboard-list" itemWidth={dims.itemWidth} gap={dims.gap}>
        {records.map(renderCard)}
      </LeaderCarousel>
    </>
  );
}
