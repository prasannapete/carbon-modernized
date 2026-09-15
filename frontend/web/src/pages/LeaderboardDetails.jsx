import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

// Same-origin path; the Vite dev proxy (see vite.config.js) forwards this to
// POST http://localhost:9202/leaderboard/get. Using a relative path avoids the
// cross-origin CORS preflight that the :9202 resource server rejects with 401.
const API_URL = '/leaderboard/get'

// Ports res/static/js/leader-board-details.js to React.
export default function LeaderboardDetails() {
  const [searchParams] = useSearchParams()
  const name = searchParams.get('name')
  const internalName = searchParams.get('internalName')

  const [rows, setRows] = useState([])
  const [start, setStart] = useState(0)
  const [loading, setLoading] = useState(true)

  function getLeaderboardDetails(startPos) {
    setLoading(true)
    fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        StatisticName: name,
        StartPosition: startPos,
      }),
    })
      .then((res) => res.json())
      .then((response) => {
        console.log(response)
        // Preserves the original (inverted) condition from the reference app.
        if (!response.success) {
          setLoading(false)
          const list = response.data?.Leaderboard || []
          setRows((prev) => [...prev, ...list])
        } else {
          setLoading(false)
        }
      })
      .catch(() => setLoading(false))
  }

  // Initial load (equivalent to init() -> getLeaderboardDetails(0))
  useEffect(() => {
    getLeaderboardDetails(0)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function handleLoadMore() {
    const next = start + 10
    setStart(next)
    getLeaderboardDetails(next)
  }

  return (
    <>
      <div
        className="col-xs-12 d-flex justify-content-center"
        style={{
          alignContent: 'center',
          alignItems: 'center',
          width: '100%',
          borderBottom: '35px',
        }}
      >
        <img style={{ maxHeight: '75px' }} src="/images/srl-logo.png" alt="SRL logo" />
      </div>
      <h1 id="leaderBoardName">{internalName}</h1>

      <table id="leader-board-details">
        <thead>
          <tr>
            <th>Alias</th>
            <th>Clasificación</th>
            <th>Puntuación</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((leaderboard, i) => (
            <tr key={`${leaderboard.PlayFabId}-${i}`}>
              <td>
                {leaderboard.DisplayName != null
                  ? leaderboard.DisplayName
                  : leaderboard.PlayFabId}
              </td>
              <td>{leaderboard.Position + 1}</td>
              <td>
                {leaderboard.StatValue < 0
                  ? (-1 * leaderboard.StatValue) / 1000
                  : leaderboard.StatValue}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="col-12">
        {loading && (
          <div className="loading-records">
            <div className="d-flex align-items-center">
              <div className="w-100">
                <div className="text-center mb-5">
                  <i className="fa fa-spinner fa-pulse fa-5x fa-fw"></i>
                </div>
                <div>
                  <h6 className="text-center">Loading</h6>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      <div className="d-flex justify-content-center align-items-center mt-3">
        <button
          id="load-more"
          className="btn btn-primary btn-load-more"
          onClick={handleLoadMore}
        >
          Load more
        </button>
      </div>
    </>
  )
}
