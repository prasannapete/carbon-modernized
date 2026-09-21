import React, {useEffect, useState} from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import '../../css/StorePerformance.css';
import axiosInstance from "../../interceptors";
import shellIcon from '../../assets/shell-icon.png';

const StorePerformance = ({ startDate, endDate,country }) => {
    const [appStoreActiveTab, setAppStoreActiveTab] = useState(null);

    const [playStoreActiveTab, setPlayStoreActiveTab] = useState(null);
    const [data, setData] = useState(null);
    useEffect(() => {
        if (startDate && endDate ) {
            fetchData();
        }
    }, [startDate, endDate,country]);

    const fetchData = async () => {
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;

        try {
            const [appStoreData,playStoreData] = await Promise.all([
                axiosInstance.post(
                    `${BASE_URL}/srl/AppStoreSaleRep/get-app-store-data`,
                    { startDate, endDate,country },
                ),
                axiosInstance.post(
                    `${BASE_URL}/srl/playStoreReport/get-play-store-data`,
                    { startDate, endDate,country },
                )
            ]);

            setData({appStoreData:appStoreData.data.data,playStoreData:playStoreData.data.data});
            if (appStoreData.data?.data?.graphData) {
                const countryList = Object.keys(appStoreData.data.data.graphData);
                setAppStoreActiveTab(countryList[0] || null);
            }
            if (playStoreData.data?.data?.graphData) {
                const countryList = Object.keys(playStoreData.data.data.graphData);
                setPlayStoreActiveTab(countryList[0] || null);
            }


        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    return (
        <div className="store-performance">
            <h2>Store Performance</h2>
            <div className="store-grid">
                <div className="store-card">
                    <div className="store-header">
                        <h3>App Store</h3>
                        <div className="country-tabs scroll-x">
                            {Object.entries(data?.appStoreData?.graphData || {}).map(([code, records]) => {
                                const countryName = records?.[0]?.countryName || code;
                                return (
                                    <button
                                        key={code}
                                        className={`tab ${appStoreActiveTab === code ? 'active' : ''}`}
                                        onClick={() => setAppStoreActiveTab(code)}
                                    >
                                        {countryName}
                                    </button>
                                );
                            })}
                        </div>
                    </div>

                    {appStoreActiveTab && data?.appStoreData?.graphData?.[appStoreActiveTab]?.length>0 ? (
                        <div className="store-card" key={appStoreActiveTab}>
                            <div className="store-content">
                                <div className="app-info">
                                    <div className="app-title">
                                        <img src={shellIcon} alt="Shell Icon" className="app-icon" />
                                        Shell Racing Legends
                                    </div>
                                    <div className="downloads-info">
                                        <div className="total-downloads">
                                            <span>TOTAL DOWNLOADS</span>
                                            <div className="downloads-number">{data.appStoreData.graphData[appStoreActiveTab].reduce((acc, val) => acc + val.downloadCount, 0)}</div>
                                        </div>
                                    </div>

                                </div>
                                <div className="chart-container">
                                    <ResponsiveContainer width="100%" height="100%">
                                        <LineChart
                                            data={data.appStoreData.graphData[appStoreActiveTab].map((item) => ({
                                                month: item.monthName,
                                                downloads: item.downloadCount
                                            }))}
                                        >
                                            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                            <XAxis dataKey="month"
                                                   axisLine={false}
                                                   tickLine={false}
                                                   tick={{ fontSize: 12, fill: '#666' }}/>
                                            <YAxis
                                                axisLine={false}
                                                tickLine={false}
                                                tick={{ fontSize: 12, fill: '#666' }}/>
                                            <Tooltip
                                                contentStyle={{
                                                    backgroundColor: 'white',
                                                    border: '1px solid #e0e0e0',
                                                    borderRadius: '4px',
                                                    fontSize: '12px'
                                                }}/>
                                            <Line
                                                type="monotone"
                                                dataKey="downloads"
                                                stroke="#007bff"
                                                strokeWidth={2}
                                                dot={{  fill: '#28a745', strokeWidth: 2, r: 4  }}
                                                activeDot={{ r: 6, fill: '#28a745' }}
                                            />
                                        </LineChart>
                                    </ResponsiveContainer>
                                </div>
                            </div>
                        </div>
                    ):<div className="chart-container">No graph data available</div>}


                </div>

                <div className="store-card">
                    <div className="store-header">
                        <h3>Play Store</h3>
                        <div className="country-tabs scroll-x">
                            {Object.entries(data?.playStoreData?.graphData || {}).map(([code, records]) => {
                                const countryName = records?.[0]?.countryName || code;
                                return (
                                    <button
                                        key={code}
                                        className={`tab ${playStoreActiveTab === code ? 'active' : ''}`}
                                        onClick={() => setPlayStoreActiveTab(code)}
                                    >
                                        {countryName}
                                    </button>
                                );
                            })}
                        </div>
                    </div>


                    <div className="store-content">
                        <div className="chart-title">Time series</div>
                        <div className="chart-container">
                            {playStoreActiveTab && data?.playStoreData?.graphData?.[playStoreActiveTab]?.length>0 ? (
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart  data={data.playStoreData.graphData[playStoreActiveTab].map((item) => ({
                                    month: item.monthName,
                                    downloads: item.downloadCount
                                }))}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                    <XAxis
                                        dataKey="month"
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fontSize: 12, fill: '#666' }}
                                    />
                                    <YAxis
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fontSize: 12, fill: '#666' }}
                                    />
                                    <Tooltip
                                        contentStyle={{
                                            backgroundColor: 'white',
                                            border: '1px solid #e0e0e0',
                                            borderRadius: '4px',
                                            fontSize: '12px'
                                        }}
                                    />
                                    <Line
                                        type="monotone"
                                        dataKey="downloads"
                                        stroke="#28a745"
                                        strokeWidth={2}
                                        dot={{ fill: '#28a745', strokeWidth: 2, r: 4 }}
                                        activeDot={{ r: 6, fill: '#28a745' }}
                                    />
                                </LineChart>
                            </ResponsiveContainer> ) : (
                                <div>No graph data available</div>
                                )}
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default StorePerformance;