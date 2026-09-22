# Leaderboard Web (React)

React (Vite) conversion of the original Spring Boot + Thymeleaf **Leaderboard Web**
application (reference: `D:\Carbon1\frontend\leaderboard-web`). The UI, behaviour, and
API calls are preserved 1:1.

## Run

```powershell
cd D:\carbon-modernized\frontend\leaderboard-web
npm install
npm run dev
```

Dev server: http://localhost:5173

### Auth & API (local Carbon Modernized)

Authentication mirrors **SRL Dashboard**: the app runs the OAuth2 Authorization Code
flow against the **local authserver** (`http://localhost:9201`, client `srl-analytics`),
stores the access token in `localStorage` (`e_access_token`), and sends it as a Bearer
token. Data comes directly from **kitkat-service** (`http://localhost:9204`), a local-auth
JWT resource server — the app no longer uses the old `:9111` session BFF or the remote
`srl-dev-auth` server.

Dev proxies (see `vite.config.js`, `.env`):
- `/api/*` → kitkat-service `:9204` (data; `/api` prefix stripped)
- `/oauth2/token` → authserver `:9201` (code→token exchange)
- the authorization redirect is a full-page navigation straight to `:9201`

**Runs on port 3001** (`npm run dev`; SRL Dashboard uses 3000, so they no longer clash).
The redirect URI is `http://localhost:3001/oauth2/callback` (`VITE_OAUTH_REDIRECT_URI` in
`.env`). For login to work, the `srl-analytics` OAuth client in the local authserver must
have `http://localhost:3001/oauth2/callback` registered as an allowed redirect URI — if it
only has `:3000`, login fails with a `redirect_uri` mismatch.

Viewer pages (leaderboard, display score, kk-race, event list) use kitkat's *public*
endpoints and load without login. Admin actions (create/edit/delete event, participants,
scores) hit protected endpoints; a 401 triggers the local-auth login automatically.

> Note: logo upload (`/events/upload-logo`) had no kitkat equivalent (the old BFF did the
> S3 upload itself), so that one action is unavailable when running against kitkat.

Prereqs: run the local `authserver` (9201) and `kitkat-service` (9204).

## Structure

```
src/
├── components/   Layout, Header, Footer, modals, LeaderCarousel
├── pages/        EventList, EventUI, AddParticipant, UpdateScore,
│                 EventDisplayScore, LeaderBoard, KkRaceLeaderBoard
├── services/     api.js (form/JSON/multipart POST helpers)
├── context/      AppContext (header pagination hook)
├── lib/          swal.js, useBootstrapModal.js
├── App.jsx       routes (match the original controller URLs)
└── main.jsx
public/           all original CSS, images, icon fonts and vendor assets (reused verbatim)
```

## Routes → original pages

| Route | Page |
| --- | --- |
| `/events/list` | Event list |
| `/events/create-event[/:id]` | Create / edit event |
| `/events/update-score[/:eventId]` | Update score |
| `/event-participants/new-participant[/:id]` | Add participant |
| `/event-scores/single-event-display-score` (`?id=`) | Single event display score |
| `/event-scores/event-display-score`, `/events/display-leader-board` | Leaderboard |
| `/kk-race-played/leader-board[/:consoleId]` | Race leaderboard |

## Build

```powershell
npm run build
```
