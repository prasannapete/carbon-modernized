import React, { useEffect, useState } from 'react';
import axiosInstance from '../../interceptors';
import {
    BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
} from 'recharts';
import '../../css/WeeklyView.css';

// Weekly dashboard view: shows the same metrics as the standard dashboard, but with one column per
// week (instead of the single "Selected Period" column) plus a "Cumulative" column, and graphs that
// group their series by week.
//
// All of the week bucketing and the per-week numbers are now computed ON THE SERVER by a single
// endpoint (/shell-events/weekly-dashboard), which reuses the exact same computeWeeks + gather logic
// as the Weekly Report Excel download. So this view makes ONE request and the numbers are guaranteed
// to match the Excel. Previously this component fired ~48 parallel per-week requests (6 sections x
// ~8 weeks); on production that exhausted the backend's connection pool, and the dropped requests
// were silently turned into 0 — which is what made some week columns show 0 / "No data available".

// The axios instance has no baseURL, so the request MUST be prefixed with the shell-analytics service
// base URL exactly like the standard dashboard components do.
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

// Guard only: when the range is empty/invalid we show a hint instead of calling the server. The real
// week buckets come from the server response (weeks[]), so this never needs to match it exactly.
const hasRange = (startStr, endStr) => {
    if (!startStr || !endStr) return false;
    const parse = (s) => { const [y, m, d] = s.split('-').map(Number); return new Date(y, m - 1, d); };
    const start = parse(startStr), end = parse(endStr);
    return !isNaN(start) && !isNaN(end) && start <= end;
};

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
// with a weekly line graph (one line per country). `rows` come straight from the server payload:
// [{ code, name, weekVals:[...], cumulative }].
function FlatWeeklySection({ title, weeks, rows: serverRows }) {
    const rows = (serverRows || [])
        .map((r) => ({
            key: r.code,
            label: `${r.name} (${r.code})`,
            weekVals: (r.weekVals || []).map((v) => Number(v) || 0),
            cumulative: Number(r.cumulative) || 0,
        }))
        .sort((a, b) => a.label.localeCompare(b.label));

    const colTotals = weeks.map((_, i) => rows.reduce((s, r) => s + (r.weekVals[i] || 0), 0));
    const cumTotal = rows.reduce((s, r) => s + r.cumulative, 0);
    const chartData = weeks.map((w, i) => {
        const o = { week: w.label };
        rows.forEach((r) => { o[r.label] = r.weekVals[i] || 0; });
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

// Nested per-country / per-car section (Cars Unlocked / RC Played / Race Played): one block per
// country with CARS | week... | Cumulative and a weekly grouped bar chart. `countries` come straight
// from the server payload: [{ code, name, cars:[{ carName, weekVals:[...], cumulative }] }].
function CarWeeklySection({ title, weeks, countries, isDuration }) {
    const fmt = isDuration ? formatDuration : fmtInt;
    const side = weeks.length <= SIDE_BY_SIDE_MAX_WEEKS;
    const list = countries || [];
    if (!list.length) return (<div className="weekly-section"><h2>{title}</h2><div className="weekly-empty">No data available for the selected filters.</div></div>);

    return (
        <div className="weekly-section">
            <h2>{title}</h2>
            {list.map((cty, ci) => {
                const name = cty.name || cty.code;
                const rows = (cty.cars || []).map((car) => ({
                    key: car.carName,
                    label: car.carName,
                    weekVals: (car.weekVals || []).map((v) => Number(v) || 0),
                    cumulative: Number(car.cumulative) || 0,
                }));
                const colTotals = weeks.map((_, i) => rows.reduce((s, r) => s + (r.weekVals[i] || 0), 0));
                const cumTotal = rows.reduce((s, r) => s + r.cumulative, 0);
                const chartData = rows.map((r) => {
                    const o = { car: r.label };
                    weeks.forEach((w, i) => { o[w.label] = Math.round(r.weekVals[i] || 0); });
                    return o;
                });
                return (
                    <div key={cty.code} className="weekly-country">
                        <div className="weekly-country-title">Country #{ci + 1} - {name} ({cty.code})</div>
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
    // null = still loading; a payload object once loaded; the error string on failure.
    const [payload, setPayload] = useState(null);
    const [error, setError] = useState(null);

    const rangeOk = hasRange(startDate, endDate);

    useEffect(() => {
        if (!rangeOk) { setPayload(null); setError(null); return; }
        let cancelled = false;
        setPayload(null);
        setError(null);
        if (startLoading) startLoading();
        axiosInstance
            .post(`${BASE_URL}/shell-events/weekly-dashboard`, { startDate, endDate, country }, {
                headers: { 'Content-Type': 'application/json' },
                withCredentials: true,
            })
            .then((r) => {
                if (cancelled) return;
                const data = r.data && r.data.data ? r.data.data : null;
                if (!data) { setError('No data returned for the selected range.'); return; }
                setPayload(data);
            })
            .catch((e) => {
                // No silent zeros: a failed request surfaces an error instead of rendering as 0.
                if (!cancelled) setError(e?.message || 'Failed to load weekly data.');
            })
            .finally(() => { if (stopLoading) stopLoading(); });
        return () => { cancelled = true; };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [startDate, endDate, country]);

    if (!rangeOk) {
        return <div className="dashboard-content"><div className="weekly-section"><div className="weekly-empty">Select a date range to see the weekly breakdown.</div></div></div>;
    }
    if (error) {
        return <div className="dashboard-content"><div className="weekly-section"><div className="weekly-empty">Could not load the weekly data: {error}. Please retry.</div></div></div>;
    }
    if (payload === null) {
        return <div className="dashboard-content"><div className="weekly-section"><div className="weekly-empty">Loading...</div></div></div>;
    }

    const weeks = payload.weeks || [];
    const sections = payload.sections || {};
    return (
        <>
            <FlatWeeklySection title="Users" weeks={weeks} rows={sections.users} />
            <FlatWeeklySection title="App Launches" weeks={weeks} rows={sections.appLaunches} />
            <FlatWeeklySection title="Brand Views" weeks={weeks} rows={sections.brandViews} />
            <CarWeeklySection title="Cars Unlocked" weeks={weeks} countries={sections.carsUnlocked} isDuration={false} />
            <CarWeeklySection title="Remote Control Played" weeks={weeks} countries={sections.rcPlayed} isDuration={true} />
            <CarWeeklySection title="Race Played" weeks={weeks} countries={sections.racePlayed} isDuration={true} />
        </>
    );
};

export default WeeklyView;
