import { API_BASE } from '../config/config';
import { getToken, requireLogin } from '../lib/auth';

// The pages call the original BFF-style paths (/events/*, /event-participants/*,
// /event-scores/*, /kk-race-played/*). Since leaderboard-web now talks directly to
// kitkat-service (a local-auth JWT resource server) instead of the :9111 session BFF,
// this module translates each call to the equivalent kitkat endpoint, attaches the
// Bearer token, and reshapes the response back to what the pages expect. Keeping the
// translation here means the page components are unchanged.

// ---- low-level kitkat call (via /api proxy -> :9204), with Bearer + 401 handling ----
async function kitkat(method, path, { json, formData } = {}) {
  const headers = {};
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const opts = { method, headers };
  if (formData !== undefined) {
    // Multipart: let the browser set Content-Type (with the multipart boundary).
    opts.body = formData;
  } else if (json !== undefined) {
    headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(json);
  }
  const res = await fetch(API_BASE + path, opts);
  if (res.status === 401 || res.status === 403) {
    requireLogin();
    throw new Error('unauthorized');
  }
  if (!res.ok) {
    const err = new Error('HTTP ' + res.status);
    err.status = res.status;
    throw err;
  }
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) return res.json();
  const text = await res.text();
  try { return JSON.parse(text); } catch { return text; }
}

// Client-side sort for the admin event list (the whole list loads at once).
function sortEvents(list, params) {
  const field = params['order[0][column]'] || params.sort;
  const dir = params['order[0][dir]'] || 'asc';
  if (!field) return list;
  const sorted = [...list].sort((a, b) => {
    const av = a[field]; const bv = b[field];
    if (av == null && bv == null) return 0;
    if (av == null) return -1;
    if (bv == null) return 1;
    if (av < bv) return -1;
    if (av > bv) return 1;
    return 0;
  });
  if (dir === 'desc') sorted.reverse();
  return sorted;
}

// kitkat pagination/sort keys differ from the BFF's DataTables params.
function pageBody(params, extra = {}) {
  const sortField = params['order[0][column]'] || params.sort || 'creationTime';
  const sortOrder = params['order[0][dir]'] || 'asc';
  return {
    current_page: '0',
    page_size: String(params.length ?? 25),
    sort_field: sortField || 'creationTime',
    sort_order: sortOrder || 'asc',
    ...extra,
  };
}

// ---- translations keyed by the BFF path the pages call ----
const handlers = {
  // Events
  // Public/live events only (isLive=1 & currently within date range): used by the
  // Header events dropdown, the leaderboard carousel and the score-display screen.
  '/events/get-all': () => kitkat('POST', '/carbon-events/get-all-events'),
  // Admin management list: ALL non-deleted events regardless of isLive or end date
  // (matches the original app's /get-events). Uses the dedicated kitkat
  // /carbon-events/get-events, which returns every non-deleted event as a flat list
  // (independent of the live-only /get-all-events feed). Sorted client-side.
  '/events/get-events': async (params) => {
    const resp = await kitkat('POST', '/carbon-events/get-events');
    const list = (resp && resp.data) || [];
    const sorted = sortEvents(list, params);
    return { success: true, recordsFiltered: sorted.length, recordsTotal: sorted.length, data: { data: sorted } };
  },
  '/events/get': (params) => kitkat('GET', `/carbon-events/${params.id}`),
  '/events/save': (dto) => kitkat('POST', '/carbon-events/save', {
    json: {
      id: dto.id,
      eventName: dto.eventName,
      eventType: dto.eventType,
      description: dto.description,
      startDate: dto.startDateStr || dto.startDate,
      endDate: dto.endDateStr || dto.endDate,
      primaryColor: dto.primaryColor,
      isLive: dto.isLive,
      scoreType: dto.scoreType,
      logoPath: dto.logoPath,
    },
  }),
  '/events/trash': (params) => kitkat('DELETE', `/carbon-events/delete/${params.id}`),
  '/events/get-leader-board-events': (params) =>
    kitkat('POST', '/carbon-events/get-leader-board-events', {
      json: { current_page: '0', page_size: String(params.length ?? 10) },
    }),
  '/events/keep-session': () => kitkat('POST', '/carbon-events/keep-session'),
  // Logo upload: multipart POST to kitkat, which stores the file and returns a servable
  // URL. Response is a list ([{success, excelUploadPath}]) matching the original BFF shape.
  '/events/upload-logo': (formData) => kitkat('POST', '/carbon-events/upload-logo', { formData }),

  // Event participants
  '/event-participants/save': (dto) => kitkat('POST', '/carbon-event-participants/save', {
    json: { id: dto.id, name: dto.name, age: dto.age, eventId: dto.eventId },
  }),
  '/event-participants/get': (params) => kitkat('GET', `/carbon-event-participants/${params.id}`),
  '/event-participants/trash': (params) => kitkat('DELETE', `/carbon-event-participants/delete/${params.id}`),
  '/event-participants/get-event-participants-by-event-id': (params) =>
    kitkat('POST', '/carbon-event-participants/get-participants-by-event-id', {
      json: pageBody(params, { eventId: String(params.eventId) }),
    }),

  // Event scores
  '/event-scores/save': async (dto) => {
    const resp = await kitkat('POST', '/carbon-events-scores/save', {
      json: { id: dto.id, participantId: dto.participantId, eventId: dto.eventId, score: dto.score, time: dto.time },
    });
    // Pages read response.eventScoresDTO; the generic save returns the saved DTO in .data.
    if (resp && !resp.eventScoresDTO && resp.data) return { ...resp, eventScoresDTO: resp.data };
    return resp;
  },
  '/event-scores/trash': (params) => kitkat('DELETE', `/carbon-events-scores/delete/${params.id}`),
  '/event-scores/get-event-score-by-event-id': (params) =>
    kitkat('POST', '/carbon-events-scores/get-scores-by-event-id', {
      json: pageBody(params, { eventId: String(params.eventId) }),
    }),
  '/event-scores/get-event-score-by-event-id-and-participant-id': (params) =>
    kitkat('POST', '/carbon-events-scores/get-scores-by-event-id-and-participant-id', {
      json: pageBody(params, { eventId: String(params.eventId), participantId: String(params.participantId) }),
    }),
  '/event-scores/export-score-to-excel': (params) =>
    kitkat('POST', '/carbon-events-scores/export-score-to-excel', {
      json: {
        eventId: String(params.eventId),
        sort: params.sort,
        order: params.order,
        scoreType: String(params.scoreType),
        length: String(params.length ?? 30),
      },
    }),

  // Kk race played
  '/kk-race-played/race-info': (params) =>
    kitkat('POST', '/events/kk-race-played/race-info', {
      json: params && params.consoleId != null ? { consoleId: String(params.consoleId) } : {},
    }),
};

function dispatch(url, params) {
  const handler = handlers[url];
  if (!handler) {
    return Promise.reject(new Error('No kitkat mapping for ' + url));
  }
  return handler(params || {});
}

// Public API used by the pages (signatures unchanged).
export function postForm(url, params = {}) {
  return dispatch(url, params);
}
export function postJson(url, obj) {
  return dispatch(url, obj);
}
export function postMultipart(url, formData) {
  // Used by /events/upload-logo: forwards the FormData to the kitkat upload endpoint.
  return dispatch(url, formData);
}
