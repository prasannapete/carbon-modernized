import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import EventList from './pages/EventList';
import EventUI from './pages/EventUI';
import UpdateScore from './pages/UpdateScore';
import AddParticipant from './pages/AddParticipant';
import EventDisplayScore from './pages/EventDisplayScore';
import LeaderBoard from './pages/LeaderBoard';
import KkRaceLeaderBoard from './pages/KkRaceLeaderBoard';
import AuthCallback from './pages/AuthCallback';
import RequireAuth from './components/RequireAuth';

// Routes match the reference Spring controller URLs exactly, so links/bookmarks keep working.
export default function App() {
  return (
    <Routes>
      {/* OAuth2 authorization-code callback (outside the app shell, not gated) */}
      <Route path="/oauth2/callback" element={<AuthCallback />} />
      <Route element={<RequireAuth><Layout /></RequireAuth>}>
        <Route path="/" element={<Navigate to="/events/list" replace />} />
        <Route path="/events/list" element={<EventList />} />
        <Route path="/events/create-event" element={<EventUI />} />
        <Route path="/events/create-event/:id" element={<EventUI />} />
        <Route path="/events/update-score" element={<UpdateScore />} />
        <Route path="/events/update-score/:eventId" element={<UpdateScore />} />
        <Route path="/events/display-leader-board" element={<LeaderBoard />} />
        <Route path="/event-scores/event-display-score" element={<LeaderBoard />} />
        <Route path="/event-scores/single-event-display-score" element={<EventDisplayScore />} />
        <Route path="/event-scores/single-event-display-score/:id" element={<EventDisplayScore />} />
        <Route path="/event-participants/new-participant" element={<AddParticipant />} />
        <Route path="/event-participants/new-participant/:id" element={<AddParticipant />} />
        <Route path="/kk-race-played/leader-board" element={<KkRaceLeaderBoard />} />
        <Route path="/kk-race-played/leader-board/:consoleId" element={<KkRaceLeaderBoard />} />
        <Route path="*" element={<Navigate to="/events/list" replace />} />
      </Route>
    </Routes>
  );
}
