import React, {useEffect, useState,useRef } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import StorePerformance from './StorePerformance';
import AppLaunches from './Downloads';
import AppLaunchesPrint from './AppLaunchesPrint';
import CarsUnlocked from './CarsUnlocked';
import CarsUnlockedPrint from './CarsUnlockedPrint';
import RemoteControlPlayed from './RemoteControlPlayed';
import RemoteControlPlayedPrint from './RemoteControlPlayedPrint';
import '../../css/Dashboard.css';
import '../../css/Filter.css';
import {useNavigate} from 'react-router-dom';
import Select from 'react-select';
import axiosInstance from "../../interceptors";
import Cookies from "js-cookie";
import {useDispatch, useSelector} from "react-redux";
import {setLogout} from "../../redux/slices/signin-slice";
import RacePlayed from "./RacePlayed";
import RacePlayedPrint from "./RacePlayedPrint";
import {setSelectedCountries} from "../../redux/slices/countryFilter-slice";
import Navbar from "./Navbar";
import CustomerStats from "./CustomerStats";
import CustomerStatsPrint from "./CustomerStatsPrint";
import EvuemeLoader from "../loaders/evueme-loader";
import { useReactToPrint } from 'react-to-print';
import carbonLogo from '../../assets/carbon-logo.png';
import html2pdf from "html2pdf.js";
import CompetitionPlayed from "./CompetitionPlayed";
import CompetitionPlayedPrint from "./CompetitionPlayedPrint";

const Dashboard = () => {
    const printRef = useRef(null);
    const formatDate = (date) => date.toLocaleDateString('en-CA');
    const [loadingCount, setLoadingCount] = useState(0);
    const [isPrinting, setIsPrinting] = useState(false);
    const startLoading = () => setLoadingCount((count) => count + 1);
    const stopLoading = () => setLoadingCount((count) => Math.max(count - 1, 0));
    const loading = loadingCount > 0;

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(),1);
    firstDayOfMonth.setHours(0, 0, 0, 0);

const selectedCountries = useSelector((state) => state.countryFilter.selectedCountries);
const [countries, setCountries] = useState([]);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [startDate, setStartDate] = useState(firstDayOfMonth);
    const [endDate, setEndDate] = useState(today);
    const [filters, setFilters] = useState({
        startDate: formatDate(firstDayOfMonth),
        endDate: formatDate(today),
        country: selectedCountries.map((c) => c.value).join(','),
        printCountry: selectedCountries.length>0 ? selectedCountries.map((c) => c.value).join(',') : countries.map((c) => c.value).join(','),
        isPrinting: false,
    });

    useEffect(() => {
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;
        const fetchCountries = async () => {
            try {
                const response = await axiosInstance.post(`${BASE_URL}/srl/country/get-countries-by-user`);
                const options = response.data.data.map((c) => ({
                    value: c.country,  // e.g., 'IN'
                    label: c.countryName,  // e.g., 'India'
                }));
                setCountries(options);
                setFilters({
                    printCountry: selectedCountries.length>0 ?selectedCountries.map((c) => c.value).join(',') : options.map((c) => c.value).join(',')
                    });
            } catch (error) {
                console.error('Failed to fetch countries:', error);
            }
        };

        fetchCountries();
    }, []);



    const handleFilter = () => {
        if (startDate && endDate) {
            setFilters({
                startDate: formatDate(startDate),
                endDate: formatDate(endDate),
                country: selectedCountries.map((c) => c.value).join(','),
                printCountry: selectedCountries.length>0 ? selectedCountries.map((c) => c.value).join(',') : countries.map((c) => c.value).join(','),
                isPrinting: false,
            });
        }
    };
    const waitForChartsToRender = () => {
      return new Promise((resolve) => {
        // wait until all canvases have non-empty content
        const checkCharts = () => {
          const charts = Array.from(document.querySelectorAll("canvas"));
          const allDrawn = charts.every((canvas) => {
            const ctx = canvas.getContext("2d");
            if (!ctx) return false;
            const pixelBuffer = new Uint32Array(
              ctx.getImageData(0, 0, canvas.width, canvas.height).data.buffer
            );
            return pixelBuffer.some((color) => color !== 0); // not blank
          });
          if (allDrawn || charts.length === 0) {
            resolve();
          } else {
            setTimeout(checkCharts, 300);
          }
        };
        checkCharts();
      });
    };
function printInNewWindow(htmlString) {
  const printWindow = window.open('', '', 'height=500, width=800');
  printWindow.document.write('<html><head><title>Print</title>');
  printWindow.document.write('<link rel="stylesheet" href="your-styles.css"/>');
  printWindow.document.write('</head><body >');
  printWindow.document.write(htmlString);
  printWindow.document.write('</body></html>');
  printWindow.document.close();
  printWindow.print();
}

function handleExportPDF() {
  const element = document.getElementById('print-content');
  html2pdf().from(element).save();
}
const handlePrint = useReactToPrint({
  contentRef: printRef,
  documentTitle: "Dashboard Report",
  onBeforeGetContent: () => {
    return new Promise((resolve) => {
      // 1️⃣ Update filters to include isPrinting
      setFilters(prev => ({
        ...prev,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
        country:selectedCountries.length>0 ? selectedCountries.map((c) => c.value).join(',') : countries,
        isPrinting: true,
      }));

      // 2️⃣ Wait a bit for re-render to complete
      setTimeout(() => {
        console.log("Ref before print:", printRef.current);
        console.log("Charts and print mode ready...");
        resolve();
      }, 500); // adjust delay if charts take longer to appear
    });
  },
  onAfterPrint: () => {
    // Reset isPrinting after printing
    setFilters(prev => ({ ...prev, isPrinting: false }));
  },
});



    const [downloading, setDownloading] = useState(false);

    const handleDownload = async () => {
        const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;
        startLoading();
        setDownloading(true);
        try {
            const response = await axiosInstance.post(
                `${BASE_URL}/shell-events/export-multi-tab-excel`,
                {
                    startDate: formatDate(startDate),
                    endDate: formatDate(endDate),
                    country: selectedCountries.map((c) => c.value).join(','),
                },
                { responseType: 'blob' }
            );

            const fileName = `shell_events_${Date.now()}_${performance.now().toFixed(0)}.xlsx`;

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Failed to download Excel:', error);
        } finally {
            stopLoading();
            setDownloading(false);
        }
    };


    const handleLogOutClick = () => {
        const AUTH_URL = process.env.REACT_APP_AUTH_URL;
        // Clear the dashboard's local session state first.
        Object.keys(Cookies.get()).forEach(cookieName => {
            Cookies.remove(cookieName);
        });
        localStorage.removeItem("authToken");
        dispatch(setLogout());
        // Top-level redirect to the auth server logout: a GET navigation sends the SRLSESSION
        // cookie so the SSO session is actually invalidated (a cross-origin XHR cannot), then the
        // auth server redirects back to the dashboard, which re-initiates a fresh login.
        window.location.href = `${AUTH_URL}/logout`;
    };

    return (
        <div className="dashboard">
            {loading && <EvuemeLoader />}
            <div className="dashboard-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Dashboard</h1>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <Navbar currentPage="dashboard" onLogout={handleLogOutClick} />
                </div>
            </div>

            <div className="filter-container">
                <h2 className="filter-title">Filter</h2>
                <div className="filter-controls">
                    <div className="filter-input-group">
                        <DatePicker
                            selected={startDate}
                            onChange={(date) => setStartDate(date)}
                            placeholderText="Start Date - By Default, This Month Start Date"
                            className="filter-input"
                            dateFormat="yyyy-MM-dd"
                            isClearable
                            showPopperArrow={false}
                        />
                    </div>

                    <div className="filter-input-group">
                        <DatePicker
                            selected={endDate}
                            onChange={(date) => setEndDate(date)}
                            placeholderText="End Date - By Default, Today"
                            className="filter-input"
                            dateFormat="yyyy-MM-dd"
                            isClearable
                            showPopperArrow={false}
                            minDate={startDate}
                        />
                    </div>
                    <div className="filter-input-group">
                        <Select
                            isMulti
                            value={selectedCountries}
                            onChange={(selectedOptions) => {
                                dispatch(setSelectedCountries(selectedOptions));
                            }}
                            options={countries}
                            placeholder="Select Countries"
                            classNamePrefix="react-select"
                        />
                    </div>

                    <button
                        onClick={handleFilter}
                        className="filter-button"
                        disabled={!startDate || !endDate}
                    >
                        Filter
                    </button>

                    <button
                        onClick={handleDownload}
                        className="filter-button"
                        disabled={!startDate || !endDate || downloading}
                    >
                        {downloading ? "Downloading..." : "Download Excel"}
                    </button>
                    <button onClick={handlePrint} className="filter-button">
                      Print Dashboard
                    </button>

                </div>
            </div>
                <div className="dashboard-content">
                    <CustomerStats startDate={filters.startDate} endDate={filters.endDate} country={filters.country}  startLoading={startLoading} stopLoading={stopLoading}/>
                    <AppLaunches startDate={filters.startDate} endDate={filters.endDate} country={filters.country} />
                    <CarsUnlocked startDate={filters.startDate} endDate={filters.endDate} country={filters.country}  startLoading={startLoading} stopLoading={stopLoading} isPrinting={filters.isPrinting}/>
                    <RemoteControlPlayed startDate={filters.startDate} endDate={filters.endDate} country={filters.country}  startLoading={startLoading} stopLoading={stopLoading}/>
                    <RacePlayed startDate={filters.startDate} endDate={filters.endDate} country={filters.country}/>
                    <CompetitionPlayed startDate={filters.startDate} endDate={filters.endDate} country={filters.country}  startLoading={startLoading} stopLoading={stopLoading}/>

                    {/*<StorePerformance startDate={filters.startDate} endDate={filters.endDate} country={filters.country}/>*/}
            </div>
            <div style={{display:'none'}}>
            <div ref={printRef} className="dashboard-content printable-dashboard-content">

                                {filters.printCountry &&
                                  filters.printCountry.split(',').map((code, index) => (
                                      <span key={index} className="country-chip">
                                        <div>
                                <div className="filter-controls">
                                    <div
                                        className="filter-input-group d-flex row justify-content-center align-items-center">
                                        <img src={carbonLogo} className="carbon-logo" alt="Logo"/>
                                       <span
                                           className="print-date-section readonly-date d-flex justify-content-center align-items-center"> Report From &nbsp;
                                           {endDate ? startDate?.toLocaleDateString('en-CA') : '—'}&nbsp;
                                           Till &nbsp;{endDate ? endDate.toLocaleDateString('en-CA') : '—'}&nbsp; For  &nbsp;{(() => {
                                               const country = countries.find((c) => c.value === code);
                                               return country ? country.label : code;
                                           })()}
                                         </span>
                                    </div>
                                </div>
                                </div>
                                          <CustomerStatsPrint startDate={filters.startDate} endDate={filters.endDate}
                                                              country={code} startLoading={startLoading}
                                                              stopLoading={stopLoading} isPrinting={true}/>
                                        <AppLaunchesPrint startDate={filters.startDate} endDate={filters.endDate}
                                                          country={code} startLoading={startLoading}
                                                          stopLoading={stopLoading} isPrinting={true}/>
                                        <CarsUnlockedPrint startDate={filters.startDate} endDate={filters.endDate}
                                                           country={code} startLoading={startLoading}
                                                           stopLoading={stopLoading} isPrinting={true}/>
                                        <RemoteControlPlayedPrint startDate={filters.startDate}
                                                                  endDate={filters.endDate} country={code}
                                                                  startLoading={startLoading} stopLoading={stopLoading}
                                                                  isPrinting={true}/>
                                          <RacePlayedPrint startDate={filters.startDate}
                                                                    endDate={filters.endDate} country={code}
                                                                    startLoading={startLoading} stopLoading={stopLoading}
                                                                    isPrinting={true}/>
                                          <CompetitionPlayedPrint startDate={filters.startDate}
                                                                  endDate={filters.endDate} country={code}
                                                                  startLoading={startLoading} stopLoading={stopLoading}
                                                                  isPrinting={true}/>

<div className="page-break"></div>
                                    </span>

                                  ))}

            </div>
            </div>
        </div>
    );
};

export default Dashboard;
