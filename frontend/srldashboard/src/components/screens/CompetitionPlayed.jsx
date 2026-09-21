import React, { useEffect, useState } from 'react';
import axiosInstance from '../../interceptors';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Legend,
} from 'recharts';
import '../../css/Downloads.css';

const CompetitionPlayed = ({ startDate, endDate,country,startLoading, stopLoading }) => {
    const [data, setData] = useState(null);
    const [lastWeekStart, setLastWeekStart] = useState(null);
    const [lastWeekEnd, setLastWeekEnd] = useState(null);

    useEffect(() => {
        if (startDate && endDate ) {
            getLastMonday(new Date())
            getLastSunday(new Date());
        }

    }, [startDate, endDate,country]);

    useEffect(() => {
        if (lastWeekStart && lastWeekEnd) {
            fetchData();
        }
    }, [lastWeekStart, lastWeekEnd, country]);

    function getLastMonday(endDate) {
        const date = new Date(endDate || new Date());
        const day = date.getDay(); // 0 = Sun, 1 = Mon, ... 6 = Sat

        // Step 1: find last Sunday
        const lastSunday = new Date(date);
        lastSunday.setDate(date.getDate() - day);

        // Step 2: previous Monday = lastSunday - 6 days
        const lastMonday = new Date(lastSunday);
        lastMonday.setDate(lastSunday.getDate() - 6);

        const formattedDate = lastMonday.toISOString().split("T")[0];
        setLastWeekStart(formattedDate);
    }
    function getLastSunday(endDate) {
        const date = new Date(endDate);
        const day = date.getDay();  // 0 = Sunday
        const diff = day;           // how many days to subtract

        date.setDate(date.getDate() - diff);
        const formattedDate =  new Date(date).toISOString().split("T")[0];

        setLastWeekEnd(formattedDate);
    }
    const fetchData = async () => {
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;
        try {
            startLoading();
            const res =await axiosInstance.post(
                    `${BASE_URL}/shell-events/race-played/get-country-wise-competition-data`,
                    { startDate:lastWeekStart, endDate:lastWeekEnd,country }
                )

            const responseData = res.data?.data || {};
            const { summary, graphData } = responseData;
            const chartData = summary.map((item) => ({
                country: item.countryName,
                total: item.total,
                unique: item.unique
            }));
            setData({
                chartData,
                summaryTable: summary
            });

        } catch (error) {
            console.error('Error fetching data:', error);
        }finally {
            stopLoading(); // stop loader
        }
    };

    const getCountryNameMap = (summaryTable) => {
        const map = {};
        summaryTable.forEach((entry) => {
            if (entry.countryCode && entry.countryName) {
                map[entry.countryCode] = entry.countryName;
            }
        });
        return map;
    };

    const transformGraphData = (graphData, codeToNameMap = {}) => {
        const mergedByDate = {};
        for (const [code, entries] of Object.entries(graphData)) {
            const countryName = codeToNameMap[code] || code;
            entries.forEach(({ date, count }) => {
                if (!mergedByDate[date]) mergedByDate[date] = { date };
                mergedByDate[date][countryName] = count;
            });
        }
        return Object.values(mergedByDate).sort((a, b) => new Date(a.date) - new Date(b.date));
    };

    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    };

    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="custom-tooltip">
                    <p className="tooltip-label">{formatDate(label)}</p>
                    {payload.map((entry, index) => (
                        <p key={index} style={{ color: entry.color }}>
                            {`${entry.dataKey}: ${entry.value}`}
                        </p>
                    ))}
                </div>
            );
        }
        return null;
    };

    const getCountriesFromData = (dataArray) => {
        if (!dataArray || dataArray.length === 0) return [];
        return Object.keys(dataArray[0]).filter(key => key !== 'date');
    };

    const colorPalette = [
        '#FF6633', '#FFB399', '#FF33FF', '#FFFF99', '#00B3E6',
        '#E6B333', '#3366E6', '#999966', '#99FF99', '#B34D4D',
        '#80B300', '#809900', '#E6B3B3', '#6680B3', '#66991A'
    ];

    const renderLines = (countries) => {
        return countries.map((country, index) => (
            <Line
                key={country}
                type="monotone"
                dataKey={country}
                stroke={colorPalette[index % colorPalette.length]}
                strokeWidth={2}
                dot={{ fill: colorPalette[index % colorPalette.length], strokeWidth: 2, r: 4 }}
                activeDot={{ r: 6, fill: colorPalette[index % colorPalette.length] }}
            />
        ));
    };

    const renderChart = (chartData) => (
        <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis
                    dataKey="country"
                    tick={{ fontSize: 11, fill: '#666' }}
                />
                <YAxis tick={{ fontSize: 12, fill: '#666' }} />
                <Tooltip />
                <Legend wrapperStyle={{ paddingTop: '20px' }} iconType="line" />
                <Line
                    type="monotone"
                    dataKey="total"
                    name="Race Attempts"
                    stroke={colorPalette[0]}
                    strokeWidth={2}
                />
                <Line
                    type="monotone"
                    dataKey="unique"
                    name="Unique Players"
                    stroke={colorPalette[1]}
                    strokeWidth={2}
                />
            </LineChart>
        </ResponsiveContainer>
    );

    const shouldHideSection = (summaryTable) => {
        if (!summaryTable || summaryTable.length === 0) return true;

        const totalAttempts = summaryTable.reduce((sum, i) => sum + i.total, 0);
        const totalUnique = summaryTable.reduce((sum, i) => sum + i.unique, 0);

        return totalAttempts === 0 && totalUnique === 0;
    };

    if (shouldHideSection(data?.summaryTable)) {
        return null;
    }

    return (
        <div className="app-launches">
            <div className="section-tabs">
                    Competition Played
            </div>
                <div className="launches-content">
                    <div className="launches-grid">
                        <div className="launches-chart">
                            <h3>Competition Played Stats</h3>
                            <div className="chart-container">
                                {data?.chartData?.length > 0
                                    ? renderChart(data.chartData)
                                    : <div>No graph data available</div>}
                            </div>
                        </div>
                        <div className="launches-summary">
                            <h3>Weekly Event Participation ( <span>{lastWeekStart} - {lastWeekEnd}</span>)</h3>

                            <table className="summary-table">
                                <thead>
                                <tr>
                                    <th>COUNTRY</th>
                                    <th>Selected Period</th>
                                    <th>Unique Players</th>
                                </tr>
                                </thead>
                                <tbody>
                                {data?.summaryTable?.map((item, index) => (
                                    <tr key={index}>
                                        <td>{item.countryName} ({item.country})</td>
                                        <td>{item.total.toLocaleString()}</td>
                                        <td>{item.unique.toLocaleString()}</td>
                                    </tr>
                                ))}
                                </tbody>
                                <tfoot>
                                <tr className="total-row">
                                    <td><strong>Grand Total</strong></td>
                                    <td>
                                        <strong>
                                            {data?.summaryTable?.reduce((sum, i) => sum + i.total, 0).toLocaleString()}
                                        </strong>
                                    </td>
                                    <td>
                                        <strong>
                                            {data?.summaryTable?.reduce((sum, i) => sum + i.unique, 0).toLocaleString()}
                                        </strong>
                                    </td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
        </div>
    );
};

export default CompetitionPlayed;
