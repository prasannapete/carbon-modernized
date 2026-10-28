import React, { useEffect, useState } from 'react';
import axiosInstance from '../../interceptors';
import {
    BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
} from 'recharts';
import '../../css/WeeklyView.css';

// Weekly dashboard view: shows the same metrics as the standard dashboard, but with one column per
// week (instead of the single "Selected Period" column) plus a "Cumulative" column, and graphs that
// group their series by week. The week buckets follow the exact same logic as the Weekly Report Excel
// download, and the tables mirror that Excel's layout. This view is self-contained so the standard
// dashboard is left exactly as-is (it does not reuse the shared .summary-table styling).

// The axios instance has no baseURL, so every request MUST be prefixed with the shell-analytics
// service base URL exactly like the standard dashboard components do. Without this prefix the POSTs
// hit the frontend origin and fail, which left Cars Unlocked / RC Played (and the others) blank.
const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;

const COLORS = [
    '#FF6633', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#8BC34A', '#00BCD4',
    '#E91E63', '#9C27B0', '#FF33FF', '#00B3E6', '#E6B333', '#3366E6', '#66991A',
];
const color = (i) => COLORS[i % COLORS.length];

// When a table has this few week columns it is narrow enough to sit beside its graph; with more
// week columns the table needs the full width (and horizontal scroll), so the graph goes below it.
const SIDE_BY_SIDE_MAX_WEEKS = 3;

const fmtInt = (n) => (Number(n) || 0).toLocaleString();
const formatDuration = (minutes) => {
    if (minutes == null || isNaN(minutes)) return '-';
    const hrs = Math.floor(minutes / 60);
    const mins = Math.round(minutes % 60);
    if (hrs > 0 && mins > 0) return `${hrs} h ${mins} min`;
    if (hrs > 0) return `${hrs} h 0 min`;
    return `0 h ${mins} min`;
};

// Split [start, end] into weeks: first week = start -> that week's Sunday, each later week Mon-Sun,
// final week capped at end. Dates are "yyyy-MM-dd" strings (what the endpoints expect). Labels like
// "21 Sep - 27 Sep" match the Excel week headings exactly.
export const computeWeeks = (startStr, endStr) => {
    const weeks = [];
    if (!startStr || !endStr) return weeks;
    const parse = (s) => { const [y, m, d] = s.split('-').map(Number); return new Date(y, m - 1, d); };
    const fmt = (dt) => `${dt.getFullYear()}-${String(dt.getMonth() + 1).padStart(2, '0')}-${String(dt.getDate()).padStart(2, '0')}`;
    const lbl = (dt) => dt.toLocaleDateString('en-GB', { day: '2-digit', month: 'short' });
    const start = parse(startStr), end = parse(endStr);
    if (isNaN(start) || isNaN(end) || start > end) return weeks;
    let ws = new Date(start);
    while (ws <= end) {
        const isoDow = ws.getDay() === 0 ? 7 : ws.getDay(); // Mon=1 .. Sun=7
        const sunday = new Date(ws);
        sunday.setDate(ws.getDate() + (7 - isoDow));
        const weekEnd = sunday > end ? new Date(end) : sunday;
        weeks.push({ start: fmt(ws), end: fmt(weekEnd), label: `${lbl(ws)} - ${lbl(weekEnd)}` });
        ws = new Date(weekEnd);
        ws.setDate(weekEnd.getDate() + 1);
    }
    return weeks;
};

// Fetch one endpoint once per week (in parallel). Returns an array aligned with `weeks` (each entry
// is the response body, or null on failure), plus a loading flag.
function useWeeklyResponses(endpoint, weeks, country, startLoading, stopLoading) {
    const [responses, setResponses] = useState(null);
    const weeksKey = weeks.map((w) => `${w.start}:${w.end}`).join(',');
    useEffect(() => {
        if (!weeks.length) { setResponses([]); return; }
        let cancelled = false;
        (async () => {
            if (startLoading) startLoading();
            try {
                const results = await Promise.all(weeks.map((w) =>
                    axiosInstance
                        .post(`${BASE_URL}${endpoint}`, { startDate: w.start, endDate: w.end, country }, {
                            headers: { 'Content-Type': 'application/json' },
                            withCredentials: true,
                        })
                        .then((r) => r.data)
                        .catch(() => null)
                ));
                if (!cancelled) setResponses(results);
            } finally {
                if (stopLoading) stopLoading();
            }
        })();
        return () => { cancelled = true; };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [endpoint, weeksKey, country]);
    return responses;
}

// Table: COUNTRY/CARS | week... | Cumulative, laid out like the Excel (blue header, name column
// left-aligned, numbers right-aligned, grand-total row) and horizontally scrollable when wide.
function WeeklyTable({ nameHeader, rows, weeks, colTotals, cumTotal, fmt }) {
    return (
        <div className="weekly-scroll">
            <table className="weekly-table">
                <thead>
                    <tr>
                        <th className="wk-name">{nameHeader}</th>
                        {weeks.map((w, i) => <th key={i}>{w.label}</th>)}
                        <th>Cumulative</th>
                    </tr>
                </thead>
                <tbody>
                    {rows.length > 0 ? rows.map((r) => (
                        <tr key={r.key}>
                            <td className="wk-name">{r.label}</td>
                            {r.weekVals.map((v, i) => <td key={i}>{fmt(v)}</td>)}
                            <td>{fmt(r.cumulative)}</td>
                        </tr>
                    )) : (
                        <tr><td className="wk-name" colSpan={weeks.length + 2}>No data available</td></tr>
                    )}
                </tbody>
                {rows.length > 0 && (
                    <tfoot>
                        <tr>
                            <td className="wk-name">Grand Total</td>
                            {colTotals.map((t, i) => <td key={i}>{fmt(t)}</td>)}
                            <td>{fmt(cumTotal)}</td>
                        </tr>
                    </tfoot>
                )}
            </table>
        </div>
    );
}

// Flat per-country section (Users / App Launches / Brand Views): one row per country across weeks,
// with a weekly line graph (one line per country).
function FlatWeeklySection({ title, endpoint, weeks, country, rowsFromResp, keyField, nameField, periodKey, cumulKey, startLoading, stopLoading }) {
    const responses = useWeeklyResponses(endpoint, weeks, country, startLoading, stopLoading);
    if (responses === null) return (<div className="weekly-section"><h2>{title}</h2><div className="weekly-empty">Loading...</div></div>);

    const map = {};
    weeks.forEach((w, i) => {
        const rows = rowsFromResp(responses[i]) || [];
        rows.forEach((r) => {
            const code = r[keyField];
            if (code == null) return;
            if (!map[code]) map[code] = { key: code, label: `${r[nameField]} (${code})`, weekVals: Array(weeks.length).fill(0), cumulative: 0 };
            map[code].weekVals[i] = Number(r[periodKey]) || 0;
            const c = Number(r[cumulKey]);
            if (!isNaN(c) && c) map[code].cumulative = c; // cumulative is all-time, identical across weeks
            map[code].label = `${r[nameField]} (${code})`;
        });
    });
    const rows = Object.values(map).sort((a, b) => a.label.localeCompare(b.label));
    const colTotals = weeks.map((_, i) => rows.reduce((s, r) => s + r.weekVals[i], 0));
    const cumTotal = rows.reduce((s, r) => s + r.cumulative, 0);
    const chartData = weeks.map((w, i) => {
        const o = { week: w.label };
        rows.forEach((r) => { o[r.label] = r.weekVals[i]; });
        return o;
    });

    // Few week columns -> table and graph side by side; many -> stacked (table above graph).
    const side = weeks.length <= SIDE_BY_SIDE_MAX_WEEKS;
    return (
        <div className="weekly-section">
            <h2>{title}</h2>
            <div className={`weekly-layout${side ? ' side' : ''}`}>
                <WeeklyTable nameHeader="COUNTRY" rows={rows} weeks={weeks} colTotals={colTotals} cumTotal={cumTotal} fmt={fmtInt} />
                {rows.length > 0 && (
                    <div className="weekly-chart">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={chartData} margin={{ top: 10, right: 30, left: 10, bottom: 10 }}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                <XAxis dataKey="week" tick={{ fontSize: 11, fill: '#666' }} />
                                <YAxis tick={{ fontSize: 12, fill: '#666' }} />
                                <Tooltip />
                                <Legend wrapperStyle={{ paddingTop: '10px' }} iconType="line" />
                                {rows.map((r, idx) => (
                                    <Line key={r.key} type="monotone" dataKey={r.label} stroke={color(idx)} strokeWidth={2} />
                                ))}
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                )}
            </div>
        </div>
    );
}

// Nested per-country / per-car section (Cars Unlocked / RC Played / Race Played):
// one block per country with CARS | week... | Cumulative and a weekly grouped bar chart.
function CarWeeklySection({ title, endpoint, weeks, country, periodKey, cumulKey, isDuration, startLoading, stopLoading }) {
    const responses = useWeeklyResponses(endpoint, weeks, country, startLoading, stopLoading);
    if (responses === null) return (<div className="weekly-section"><h2>{title}</h2><div className="weekly-empty">Loading...</div></div>);

    const countryName = {};
    const data = {}; // code -> carName -> { weekVals, cumulative }
    weeks.forEach((w, i) => {
        const d = responses[i] && responses[i].data;
        if (!d) return;
        const gd = d.graphData || {};
        Object.keys(gd).forEach((code) => { const arr = gd[code] || []; if (arr[0] && arr[0].countryName) countryName[code] = arr[0].countryName; });
        const summary = Array.isArray(d.summary) ? d.summary : [];
        summary.forEach((r) => {
            const code = r.country; const car = r.carName;
            if (code == null || car == null) return;
            if (!data[code]) data[code] = {};
            if (!data[code][car]) data[code][car] = { weekVals: Array(weeks.length).fill(0), cumulative: 0 };
            data[code][car].weekVals[i] = Number(r[periodKey]) || 0;
            const c = Number(r[cumulKey]);
            if (!isNaN(c) && c) data[code][car].cumulative = c;
        });
    });

    const codes = Object.keys(data);
    const fmt = isDuration ? formatDuration : fmtInt;
    const side = weeks.length <= SIDE_BY_SIDE_MAX_WEEKS;
    if (!codes.length) return (<div className="weekly-section"><h2>{title}</h2><div className="weekly-empty">No data available for the selected filters.</div></div>);

    return (
        <div className="weekly-section">
            <h2>{title}</h2>
            {codes.map((code, ci) => {
                const cars = Object.keys(data[code]);
                const name = countryName[code] || code;
                const rows = cars.map((car) => ({ key: car, label: car, weekVals: data[code][car].weekVals, cumulative: data[code][car].cumulative }));
                const colTotals = weeks.map((_, i) => cars.reduce((s, car) => s + data[code][car].weekVals[i], 0));
                const cumTotal = cars.reduce((s, car) => s + data[code][car].cumulative, 0);
                const chartData = cars.map((car) => {
                    const o = { car };
                    weeks.forEach((w, i) => { o[w.label] = Math.round(data[code][car].weekVals[i]); });
                    return o;
                });
                return (
                    <div key={code} className="weekly-country">
                        <div className="weekly-country-title">Country #{ci + 1} - {name} ({code})</div>
                        <div className={`weekly-layout${side ? ' side' : ''}`}>
                            <WeeklyTable nameHeader="CARS" rows={rows} weeks={weeks} colTotals={colTotals} cumTotal={cumTotal} fmt={fmt} />
                            <div className="weekly-chart">
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={chartData} margin={{ top: 20, right: 30, left: 10, bottom: 20 }}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                        <XAxis dataKey="car" tick={{ fontSize: 10, fill: '#666' }} />
                                        <YAxis tick={{ fontSize: 12, fill: '#666' }} />
                                        <Tooltip />
                                        <Legend wrapperStyle={{ paddingTop: '10px' }} />
                                        {weeks.map((w, i) => <Bar key={i} dataKey={w.label} fill={color(i)} />)}
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

const WeeklyView = ({ startDate, endDate, country, startLoading, stopLoading }) => {
    const weeks = computeWeeks(startDate, endDate);
    if (!weeks.length) {
        return <div className="dashboard-content"><div className="weekly-section"><div className="weekly-empty">Select a date range to see the weekly breakdown.</div></div></div>;
    }
    const common = { weeks, country, startLoading, stopLoading };
    return (
        <>
            <FlatWeeklySection
                title="Users"
                endpoint="/shell-events/car-unlocked/get-cars-average"
                rowsFromResp={(d) => (d ? d.data : []) || []}
                keyField="countryCode" nameField="countryName" periodKey="totalUsers" cumulKey="cummulativeUsers"
                {...common}
            />
            <FlatWeeklySection
                title="App Launches"
                endpoint="/shell-events/app-launched/get-country-wise-data"
                rowsFromResp={(d) => (d && d.data ? d.data.summaryTable : []) || []}
                keyField="country" nameField="countryName" periodKey="launches" cumulKey="cumulative"
                {...common}
            />
            <FlatWeeklySection
                title="Brand Views"
                endpoint="/shell-events/brand-viewed-garage/get-country-wise-data"
                rowsFromResp={(d) => (d && d.data ? d.data.summaryTable : []) || []}
                keyField="country" nameField="countryName" periodKey="views" cumulKey="cumulative"
                {...common}
            />
            <CarWeeklySection
                title="Cars Unlocked"
                endpoint="/shell-events/car-unlocked/get-car-unlocked-data"
                periodKey="unlocked" cumulKey="cumulative" isDuration={false}
                {...common}
            />
            <CarWeeklySection
                title="Remote Control Played"
                endpoint="/shell-events/rc-played/get-country-wise-data"
                periodKey="totalRcDuration" cumulKey="cumulativeRcDuration" isDuration={true}
                {...common}
            />
            <CarWeeklySection
                title="Race Played"
                endpoint="/shell-events/race-played/get-country-wise-data"
                periodKey="totalRcDuration" cumulKey="cumulativeRcDuration" isDuration={true}
                {...common}
            />
        </>
    );
};

export default WeeklyView;
