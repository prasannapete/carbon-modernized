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
import EvuemeLoader from "../loaders/evueme-loader";

const AppLaunches = ({ startDate, endDate,country}) => {
    const [activeTab, setActiveTab] = useState('launches');
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
            const [appLaunchRes, brandViewRes] = await Promise.all([
                axiosInstance.post(
                    `${BASE_URL}/shell-events/app-launched/get-country-wise-data`,
                    { startDate, endDate,country }
                ),
                axiosInstance.post(
                    `${BASE_URL}/shell-events/brand-viewed-garage/get-country-wise-data`,
                    { startDate, endDate,country }
                ),
            ]);

            const appLaunchMap = getCountryNameMap(appLaunchRes.data.data.summaryTable);
            const brandViewMap = getCountryNameMap(brandViewRes.data.data.summaryTable);

            const appLaunchData = transformGraphData(appLaunchRes.data.data.countryWiseGraphData, appLaunchMap);
            const brandViewData = transformGraphData(brandViewRes.data.data.countryWiseGraphData, brandViewMap);

            setData({
                appLaunches: {
                    chartData: appLaunchData,
                    summaryTable: appLaunchRes.data.data.summaryTable,
                    grandTotal: appLaunchRes.data.data.grandTotal
                },
                brandViews: {
                    chartData: brandViewData,
                    summaryTable: brandViewRes.data.data.summaryTable,
                    grandTotal: brandViewRes.data.data.grandTotal
                }
            });

        } catch (error) {
            console.error('Error fetching data:', error);
        }finally {
            setIsLoading(false); // stop loader
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

    const renderChart = (chartData) => {
        const countries = getCountriesFromData(chartData);
        return (
            <ResponsiveContainer width="100%" height="100%">
                <LineChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis
                        dataKey="date"
                        axisLine={false}
                        tickLine={false}
                        tick={{ fontSize: 11, fill: '#666' }}
                        tickFormatter={formatDate}
                    />
                    <YAxis
                        axisLine={false}
                        tickLine={false}
                        tick={{ fontSize: 12, fill: '#666' }}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend wrapperStyle={{ paddingTop: '20px' }} iconType="line" />
                    {renderLines(countries)}
                </LineChart>
            </ResponsiveContainer>
        );
    };

    return (
        <div className="app-launches">
            {isLoading && <EvuemeLoader />}
            <div className="section-tabs">
                <button
                    className={`tab ${activeTab === 'launches' ? 'active' : ''}`}
                    onClick={() => setActiveTab('launches')}
                >
                    App Launches
                </button>
                <button
                    className={`tab ${activeTab === 'brand' ? 'active' : ''}`}
                    onClick={() => setActiveTab('brand')}
                >
                    Brand Views
                </button>
            </div>

            {activeTab === 'launches' && (
                <div className="launches-content">
                    <div className="launches-grid">
                        <div className="launches-chart">
                            <h3>App Launches Stats</h3>
                            <div className="chart-container">
                                {data?.appLaunches?.chartData?.length > 0
                                    ? renderChart(data.appLaunches.chartData)
                                    : <div>No graph data available</div>}
                            </div>
                        </div>
                        <div className="launches-summary">
                            <h3>App Launches Summary</h3>

                            <table className="summary-table">
                                <thead>
                                <tr>
                                    <th>COUNTRY</th>
                                    <th>Selected Period</th>
                                    <th>Cumulative</th>
                                </tr>
                                </thead>
                                <tbody>
                                {data?.appLaunches?.summaryTable?.map((item, index) => (
                                    <tr key={index}>
                                        <td>{item.countryName} ({item.country})</td>
                                        <td>{item.launches.toLocaleString()}</td>
                                        <td>{item.cumulative.toLocaleString()}</td>
                                    </tr>
                                ))}
                                </tbody>
                                <tfoot>
                                <tr className="total-row">
                                    <td><strong>Grand Total</strong></td>
                                    <td>
                                        <strong>
                                            {data?.appLaunches?.summaryTable
                                                ?.reduce((sum, item) => sum + item.launches, 0)
                                                .toLocaleString()}
                                        </strong>
                                    </td>
                                    <td>
                                        <strong>
                                            {data?.appLaunches?.summaryTable
                                                ?.reduce((sum, item) => sum + item.cumulative, 0)
                                                .toLocaleString()}
                                        </strong>
                                    </td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === 'brand' && (
                <div className="launches-content">
                    <div className="launches-grid">
                        <div className="launches-chart">
                            <h3>Brand Views Stats</h3>
                            <div className="chart-container">
                                {data?.brandViews?.chartData?.length > 0
                                    ? renderChart(data.brandViews.chartData)
                                    : <div>No graph data available</div>}
                            </div>
                        </div>
                        <div className="launches-summary">
                            <h3>Brand Views Summary</h3>
                            <table className="summary-table">
                                <thead>
                                <tr>
                                    <th>COUNTRY</th>
                                    <th>Selected Period</th>
                                    <th>Cumulative</th>
                                </tr>
                                </thead>
                                <tbody>
                                {data?.brandViews?.summaryTable?.map((item, index) => (
                                    <tr key={index}>
                                        <td>{item.countryName} ({item.country})</td>
                                        <td>{item.views.toLocaleString()}</td>
                                        <td>{item.cumulative.toLocaleString()}</td>
                                    </tr>
                                ))}
                                </tbody>
                                <tfoot>

                                <tr className="total-row">
                                    <td><strong>Grand Total</strong></td>
                                    <td>
                                        <strong>
                                            {data?.brandViews?.summaryTable
                                                ?.reduce((sum, item) => sum + item.views, 0)
                                                .toLocaleString()}
                                        </strong>
                                    </td>
                                    <td>
                                        <strong>
                                            {data?.brandViews?.summaryTable
                                                ?.reduce((sum, item) => sum + item.cumulative, 0)
                                                .toLocaleString()}
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
};

export default AppLaunches;
