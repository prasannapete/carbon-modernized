import { Routes, Route } from 'react-router-dom'

import Leaderboard from './pages/Leaderboard.jsx'
import LeaderboardDetails from './pages/LeaderboardDetails.jsx'
import UserDataFetch from './pages/UserDataFetch.jsx'

// Routes mirror the original Spring HomeController mappings:
//   "/" and "/leader-board"      -> leaderboard page
//   "/leader-board-details"      -> leaderboard details page
//   "/user-data"                 -> user data fetch page
export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Leaderboard />} />
      <Route path="/leader-board" element={<Leaderboard />} />
      <Route path="/leader-board-details" element={<LeaderboardDetails />} />
      <Route path="/user-data" element={<UserDataFetch />} />
    </Routes>
  )
}
