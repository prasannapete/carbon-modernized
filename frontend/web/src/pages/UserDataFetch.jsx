import { useState } from 'react'

// Ports res/static/js/user-data-fetch.js to React.
// XLSX (SheetJS) is loaded from the CDN in index.html and available as window.XLSX,
// matching the original web app.

const SECRET_KEY = '5QD5UDQEKSPD7MQ7XGE636G7COMW4CR6QTUCGCK7FCW9MBA5FU'

const segments = [
  { value: 'C26096067A70A669', label: 'Mexico users' },
  { value: 'B0B8E8EA399E2576', label: 'Spain users' },
]

export default function UserDataFetch() {
  const [segmentId, setSegmentId] = useState('')

  function enrichPlayer(player, unlockedCars) {
    return {
      PlayerId: player.PlayerId,
      DisplayName: player.DisplayName,
      TitleId: player.TitleId,
      PublisherId: player.PublisherId,
      Origination: player.Origination,
      Created: player.Created,
      LastLogin: player.LastLogin,
      CountryCode: player.Locations?.LastLogin?.CountryCode || '',
      City: player.Locations?.LastLogin?.City || '',
      Latitude: player.Locations?.LastLogin?.Latitude || '',
      Longitude: player.Locations?.LastLogin?.Longitude || '',
      Platform: player.LinkedAccounts?.[0]?.Platform || '',
      PlatformUserId: player.LinkedAccounts?.[0]?.PlatformUserId || '',
      UnlockedCars: unlockedCars,
    }
  }

  async function fetchUnlockedCarsInBatches(players) {
    const batchSize = 500
    const delayMs = 500
    const allResults = []

    async function fetchBatch(batch) {
      const promises = batch.map((player) =>
        fetch('https://18B72.playfabapi.com/Admin/GetUserData', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-SecretKey': SECRET_KEY,
          },
          body: JSON.stringify({ PlayFabId: player.PlayerId }),
        })
          .then((res) => res.json())
          .then((response) => {
            let unlockedCarsValue = ''
            if (response.data?.Data?.unlockedCars?.Value) {
              try {
                const cars = JSON.parse(response.data.Data.unlockedCars.Value)
                unlockedCarsValue = cars.join(', ')
              } catch (e) {
                console.warn('Invalid unlockedCars JSON for', player.PlayerId)
              }
            }
            return enrichPlayer(player, unlockedCarsValue)
          })
          .catch(() => enrichPlayer(player, '')),
      )

      const results = await Promise.all(promises)
      allResults.push(...results)
    }

    for (let i = 0; i < players.length; i += batchSize) {
      const batch = players.slice(i, i + batchSize)
      await fetchBatch(batch)
      await new Promise((resolve) => setTimeout(resolve, delayMs))
    }

    // Once all batches are done, build and download the Excel workbook.
    const XLSX = window.XLSX
    const worksheet = XLSX.utils.json_to_sheet(allResults)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Players')
    const segmentLabel =
      segments.find((s) => s.value === segmentId)?.label || segmentId
    XLSX.writeFile(workbook, 'PlayFab_' + segmentLabel + '.xlsx')
  }

  function fetchPage(token, allPlayers) {
    fetch('https://18B72.playfabapi.com/Admin/GetPlayersInSegment', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-SecretKey': SECRET_KEY,
      },
      body: JSON.stringify({
        SegmentId: segmentId,
        ContinuationToken: token,
      }),
    })
      .then((res) => res.json())
      .then((response) => {
        const players = response.data?.PlayerProfiles || []
        allPlayers.push(...players)

        if (response.data.ContinuationToken) {
          fetchPage(response.data.ContinuationToken, allPlayers)
        } else {
          fetchUnlockedCarsInBatches(allPlayers)
        }
      })
      .catch((err) => {
        console.error('Fetch error:', err)
      })
  }

  function handleDownload() {
    const allPlayers = []
    fetchPage(null, allPlayers)
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
      <div className="container mt-5 text-center">
        <h4>Select Segment and Download Data</h4>
        <div className="row d-flex justify-content-center mb-3">
          <div className="col-md-4">
            <select
              className="form-select"
              id="segmentId"
              value={segmentId}
              onChange={(e) => setSegmentId(e.target.value)}
            >
              <option value="" disabled>
                Select Segment
              </option>
              {segments.map((s) => (
                <option key={s.value} value={s.value}>
                  {s.label}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-4">
            <button
              className="btn btn-primary"
              id="download-excel"
              onClick={handleDownload}
            >
              Download Excel
            </button>
          </div>
        </div>
      </div>
    </>
  )
}
