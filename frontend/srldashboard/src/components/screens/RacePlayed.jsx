import React, { useEffect,useState } from 'react';
import {BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, Cell} from 'recharts';
import '../../css/RemoteControlPlayed.css';
import axiosInstance from "../../interceptors";
import { ChevronDown, ChevronUp } from 'lucide-react';
import EvuemeLoader from "../loaders/evueme-loader";

const RacePlayed = ({ startDate, endDate,country}) => {
    const [expandedCountry, setExpandedCountry] = useState('country1');
    const [data, setData] = useState(null);
    const [isLoading, setIsLoading] = useState(false);


    useEffect(() => {
        if (startDate && endDate ) {
            fetchData();
        }
    }, [startDate, endDate,country]);

    const fetchData = async () => {
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;

        try {
            setIsLoading(true);
            const [rcPlayed] = await Promise.all([
                axiosInstance.post(
                    // `${BASE_URL}/shell-analytics/race-played-summary/summary-by-date-range`,
                    `${BASE_URL}/shell-events/race-played/get-country-wise-data`,
                    { startDate, endDate,country },
                    {
                        headers: { 'Content-Type': 'application/json' },
                        withCredentials: true
                    }
                )
            ]);

            setData(rcPlayed.data);

        } catch (error) {
            console.error('Error fetching data:', error);
        }finally {
            setIsLoading(false); // stop loader
        }
    };

    const formatDuration = (minutes) => {
        if (isNaN(minutes)) return "-";

        const hrs = Math.floor(minutes / 60);
        const mins = Math.round(minutes % 60);

        if (hrs > 0 && mins > 0) return `${hrs} h ${mins} min`;
        if (hrs > 0) return `${hrs} h 0 min`;
        return `0 h ${mins} min`;
    };

    const toggleCountry = (country) => {
        setExpandedCountry(expandedCountry === country ? null : country);
    };


    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="custom-tooltip">
                    <p className="tooltip-label">{label}</p>
                    {payload.map((entry, index) => (
                        <p key={index} style={{ color: entry.color }}>
                            {`${entry.dataKey}: ${entry.value.toLocaleString()}`}
                        </p>
                    ))}
                </div>
            );
        }
        return null;
    };
    if (!data || typeof data.data.graphData !== 'object' || !data.data.grandTotal || !Array.isArray(data.data.summary)) {
        return <div>Loading...</div>;
    }
    const { graphData, summary, grandTotal } = data.data;
    const colorPalette = [
        '#FF6384', '#36A2EB', '#FFCE56',
        '#4BC0C0', '#9966FF', '#FF9F40',
        '#8BC34A', '#00BCD4', '#E91E63',
        '#9C27B0'
    ];
    const getColor = (index) => {
        // Use fixed palette cyclically, fallback to random if needed
        return colorPalette[index % colorPalette.length];
    };

    return (
    <div className="remote-control-played">
        {isLoading && <EvuemeLoader />}
        <h2>Race Played</h2>

        {graphData && typeof graphData === 'object' && Object.keys(graphData).length > 0 ? (
            Object.keys(graphData).map((countryCode, index) => {
                const chartData = (graphData[countryCode] || []).map(item => {
                    const raceDurationMinutes = item.totalRcDuration ? item.totalRcDuration  : 0;
                    const cumulativeMinutes = item.cumulativeRcDuration ? item.cumulativeRcDuration : 0;
                    const finalDuration = formatDuration(raceDurationMinutes).replace("min","").replace("hr",".");
                    const finalCumulative = formatDuration(cumulativeMinutes).replace("min","").replace("hr",".");
                    return {
                        car: item.carName,
                        RaceDuration: Math.round(raceDurationMinutes),
                        Cumulative: Math.round(cumulativeMinutes)
                    };
                });


            const groupedSummary = summary.reduce((acc, curr) => {
                const { country, ...rest } = curr;
                if (!acc[country]) acc[country] = [];
                acc[country].push(rest);
                return acc;
            }, {});

            const summaryData = groupedSummary[countryCode];
            const grand = grandTotal;
            const records = graphData[countryCode] || [];
            const countryName = records[0]?.countryName || countryCode;

            return (
                <div key={countryCode} className="country-section">
                    <div
                        className={`country-header ${expandedCountry === countryCode ? 'expanded' : ''}`}
                        onClick={() => toggleCountry(countryCode)}
                    >
                        <span>Country #{index + 1} - {countryName} ({countryCode})</span>
                        <span className="expand-icon">
                {expandedCountry === countryCode ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
              </span>
                    </div>

                    {expandedCountry === countryCode && (
                        <div className="country-content">
                            <div className="cars-grid">
                                <div className="cars-chart">
                                    <h3>Stats</h3>
                                    <div className="chart-title">Car-wise Race Played and Cumulative Stats</div>
                                    <div className="chart-container">
                                        <ResponsiveContainer width="100%" height={300}>
                                            <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                                                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                                <XAxis
                                                    dataKey="car"
                                                    tick={{ fontSize: 10, fill: '#666' }}
                                                />
                                                <YAxis
                                                    tick={{ fontSize: 12, fill: '#666' }}
                                                />
                                                <Tooltip content={<CustomTooltip />} />
                                                <Legend wrapperStyle={{ paddingTop: '20px' }} />
                                                <Bar dataKey="RaceDuration" name="Race Duration">
                                                    {chartData.map((entry, idx) => (
                                                        <Cell key={`cell-${idx}`} fill={getColor(idx)} />
                                                    ))}
                                                </Bar>
                                            </BarChart>
                                        </ResponsiveContainer>
                                    </div>
                                </div>

                                <div className="cars-summary">
                                    <h3>Summary</h3>
                                    <table className="summary-table">
                                        <thead>
                                        <tr>
                                            <th>CARS</th>
                                            <th>Selected Period</th>
                                            <th>Cumulative</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {summaryData.map((car, idx) => (
                                            <tr key={idx}>
                                                <td>{car.carName}</td>
                                                <td>{car.totalRacDurationInHours}</td>
                                                <td>{car.cumulativeRacDurationInHours}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                        <tfoot>
                                        <tr className="total-row">
                                            <td><strong>Grand Total</strong></td>
                                            <td>
                                                <strong>
                                                    {
                                                        formatDuration(
                                                            summaryData.reduce((sum, item) => sum + item.totalRcDuration, 0)
                                                        )
                                                    }
                                                </strong>
                                            </td>
                                            <td>
                                                <strong>
                                                    {
                                                        formatDuration(
                                                            summaryData.reduce((sum, item) => sum + item.cumulativeRcDuration, 0)
                                                        )
                                                    }
                                                </strong>
                                            </td>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            );
        })) : (
            <div className="chart-container">No country data available</div>
        )}
    </div>

);
};

export default RacePlayed;