import React, {useEffect, useState} from "react";
import '../../css/Downloads.css';
import '../../css/print.css';
import axiosInstance from "../../interceptors";

const CustomerStats = ({ startDate, endDate,country,startLoading, stopLoading,ref }) => {
    const [data, setData] = useState([]);
    useEffect(() => {
        if (startDate && endDate ) {
            fetchData();
        }
    }, [startDate, endDate,country]);
    const fetchData = async () =>{
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;
        try{
            startLoading();
            const response = await axiosInstance.post(`${BASE_URL}/shell-events/car-unlocked/get-cars-average`,
                { startDate, endDate, country });
            setData(response.data?.data || []);
        }
        catch (error) {
            console.error(error);
        }finally {
            stopLoading(); // stop loader
        }
    }
    const totalUsers = data.reduce((total, item) => total + (item.totalUsers || 0), 0);
    const cummulativeUsers = data.reduce((total, item) => total + (item.cummulativeUsers || 0), 0);
    return (
        <div className="app-launches" ref={ref}>
            <h3 className="fw-bold mb-4">Users</h3>
            <div className="row">
                {/* Left Table */}
                <div className="col-md-6">
                    <div className="launches-summary">
                        <table className="printable-summary-table summary-table">
                            <thead>
                            <tr>
                                <th>COUNTRY</th>
                                <th>Selected Period</th>
                                <th>Cumulative</th>
                            </tr>
                            </thead>
                            <tbody className="average-length">
                            {data?.length > 0 ? (
                                data.map((item) => (
                                    <tr key={item.countryId}>
                                        <td>{item.countryName} ({item.countryCode})</td>
                                        <td>{item.totalUsers}</td>
                                        <td>{item.cummulativeUsers}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="2" className="text-center">No data available</td>
                                </tr>
                            )}
                            </tbody>
                            <tfoot>
                            <tr className="total-row print-total-row">
                                <td><strong>Grand Total</strong></td>
                                <td>
                                    <strong>
                                        {totalUsers}
                                    </strong>
                                </td>
                                <td>
                                    <strong>
                                        {cummulativeUsers}
                                    </strong>
                                </td>

                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

                <div className="col-md-6 average-stats printable-average-stats mt-5">
                    {data.length > 0 ? (
                        data.map((item) => (
                            <div
                                key={item.countryId}
                                className="mb-3"
                            >
                                <h5 className="fw-bold customer-header">{item.countryName} ({item.countryCode})</h5>
                                <div className="d-flex justify-content-between mb-1">
                                    <span>Average Cars</span>
                                    <span>{item.avgCars?.toFixed(2) || 0}</span>
                                </div>
                                <div className="d-flex justify-content-between mb-1">
                                    <span>Average Remote Control Played (Minutes)</span>
                                    <span>{item.avgRC ? (item.avgRC / 60).toFixed(2) : 0}</span>
                                </div>
                                <div className="d-flex justify-content-between">
                                    <span>Average Race Played (Minutes)</span>
                                    <span>{item.avgRace ? (item.avgRace / 60).toFixed(2) : 0}</span>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="text-center">No data available</div>
                    )}
                </div>
            </div>
        </div>

    );
};

export default CustomerStats;
