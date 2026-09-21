import React, { useEffect, useState } from 'react';
import axiosInstance from '../../interceptors';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend
} from 'recharts';
import '../../css/print.css';
import '../../css/Downloads.css';
import { ChevronDown, ChevronUp } from 'lucide-react';

const CompetitionPlayedPrint = ({ startDate, endDate, country, startLoading, stopLoading, isPrinting }) => {
    const [data, setData] = useState(null);
    const [expandedCountry, setExpandedCountry] = useState('country1');

    const [lastWeekStart, setLastWeekStart] = useState(null);
    const [lastWeekEnd, setLastWeekEnd] = useState(null);

    useEffect(() => {
        if (startDate && endDate) {
            getLastMonday(new Date())
            getLastSunday(new Date());
        }
    }, [startDate, endDate, country]);

    useEffect(() => {
        if (lastWeekStart && lastWeekEnd) {
            fetchData();
        }
    }, [lastWeekStart, lastWeekEnd, country]);
    function getLastMonday(endDate) {
        const date = new Date(endDate || new Date());
        const day = date.getDay();      // 0 = Sun, 1 = Mon, ... 6 = Sat

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
            const res = await axiosInstance.post(
                `${BASE_URL}/shell-events/race-played/get-country-wise-competition-data`,
                { startDate:lastWeekStart, endDate:lastWeekEnd, country },
                { headers: { 'Content-Type': 'application/json' }, withCredentials: true }
            );

            const responseData = res.data?.data || {};
            const { summary, graphData } = responseData;

            // Filter only the selected country
            const filteredSummary = summary?.filter((item) => !country || item.country === country) || [];

            // Transform graphData (expected format: { countryCode: [{ date, total, unique }] })
            const selectedGraph = graphData?.[country] || [];
            const chartData = selectedGraph.map(entry => ({
                date: entry.date,
                total: entry.total,
                unique: entry.unique,
            }));

            setData({
                chartData,
                summaryTable: filteredSummary,
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            stopLoading();
        }
    };

    const toggleCountry = (country) => {
        setExpandedCountry(expandedCountry === country ? null : country);
    };

    if (!data || !data.chartData) {
        return <div>Loading...</div>;
    }

    const { chartData, summaryTable } = data;

    const colorPalette = ['#FF6633', '#FFB399', '#FF33FF', '#FFFF99', '#00B3E6',
        '#E6B333', '#3366E6', '#999966', '#99FF99', '#B34D4D',
        '#80B300', '#809900', '#E6B3B3', '#6680B3', '#66991A'];

    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    };
    const getCountriesFromData = (dataArray) => {
        if (!dataArray || dataArray.length === 0) return [];
        return Object.keys(dataArray[0]).filter(key => key !== 'date');
    };
    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="custom-tooltip">
                    <p className="tooltip-label">{formatDate(label)}</p>
                    {payload.map((entry, index) => (
                        <p key={index} style={{ color: entry.color }}>
                            {`${entry.name}: ${entry.value}`}
                        </p>
                    ))}
                </div>
            );
        }
        return null;
    };
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
    const renderChart = (chartData) => {
        const countries = getCountriesFromData(chartData);
        return (
            <LineChart width={700} height={300} data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 100 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis
                    dataKey="country"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 11, fill: '#666' }}
                />
                <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 12, fill: '#666' }}
                />
                <Tooltip content={<CustomTooltip />} />
                <Legend  wrapperStyle={{ marginTop: 20,transform: 'translateY(15px)' }}  iconType="line" />
                <Line
                    type="monotone"
                    dataKey="total"
                    name="Race Attempts"
                    stroke={colorPalette[0]}
                    strokeWidth={2}
                />
            </LineChart>
        );
    };

    const shouldHideSection = (summaryTable) => {
        if (!summaryTable || summaryTable.length === 0) return true;

        const totalAttempts = summaryTable.reduce((sum, i) => sum + i.total, 0);
        const totalUnique = summaryTable.reduce((sum, i) => sum + i.unique, 0);

        return totalAttempts === 0 && totalUnique === 0;
    };

    if (shouldHideSection(summaryTable)) {
        return null;
    }

    return (
        <div className="app-launches">
            <div className="launches-content">
                <div className="launches-grid">
                    <div className="launches-chart launches-chart-printable">
                        <h3>Competition Played Stats</h3>
                        <div className="chart-container">
                            {chartData?.length > 0
                                ? renderChart(chartData)
                                : <div>No graph data available</div>}
                        </div>
                    </div>
                        {/* Summary Table */}
                        <div className="launches-summary" style={{marginBottom: '10px'}}>
                            <h3>Weekly Event Participation ( <span>{lastWeekStart} - {lastWeekEnd}</span>)</h3>
                            <table className="summary-table printable-summary-table">
                                <thead>
                                <tr>
                                    <th>Country</th>
                                    <th>Selected Period</th>
                                    <th>Unique Players</th>
                                </tr>
                                </thead>
                                <tbody>
                                {summaryTable.map((row, idx) => (
                                    <tr key={idx}>
                                        <td>{row.countryName} ({row.country})</td>
                                        <td>{row.total.toLocaleString()}</td>
                                        <td>{row.unique.toLocaleString()}</td>
                                    </tr>
                                ))}
                                </tbody>
                                <tfoot>
                                <tr className="total-row">
                                    <td><strong>Grand Total</strong></td>
                                    <td>
                                        <strong>
                                            {summaryTable.reduce((sum, i) => sum + i.total, 0).toLocaleString()}
                                        </strong>
                                    </td>
                                    <td>
                                        <strong>
                                            {summaryTable.reduce((sum, i) => sum + i.unique, 0).toLocaleString()}
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

export default CompetitionPlayedPrint;
