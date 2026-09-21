import React, {useEffect, useState} from 'react';
import '../../css/Settings.css';
import axiosInstance from "../../interceptors";
import {useNavigate} from "react-router-dom";
import Cookies from "js-cookie";
import {setLogout} from "../../redux/slices/signin-slice";
import {useDispatch} from "react-redux";
import Navbar from "./Navbar";


const Settings = () => {
    const [selectedCountry, setSelectedCountry] = useState('');
    const [cars, setCars] = useState([]);
    const [countries, setCountries] = useState([]);
    const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newName, setNewName] = useState('');
    const [newActive, setNewActive] = useState(false);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);


    useEffect(() => {

        const fetchCountries = async () => {
            try {
                const response = await axiosInstance.post(`${BASE_URL}/srl/country/get-countries`);
                setCountries(response.data.data);
            } catch (error) {
                console.error('Failed to fetch countries:', error);
            }
        };

        fetchCountries();
    }, []);

    useEffect(() => {
        if (!selectedCountry) {
            setCars([]);
            return;
        }

        const fetchCars = async () => {
            try {
                const response = await axiosInstance.post(`${BASE_URL}/srl/master-cars/get-cars-unlocked`,{"countryCode":selectedCountry});
                setCars(response.data.data); // Assuming data is an array of cars
            } catch (error) {
                console.error('Error fetching cars:', error);
            }
        };

        fetchCars();
    }, [selectedCountry,loading]);

    const handleToggle = async (id,carId, newValue) => {
        try {
            const response=await axiosInstance.post(`${BASE_URL}/srl/master-cars/update-status`, {
                "id":id,
                "country":selectedCountry,
                "isActive": newValue ? 1 : 0
            });

            // Update local state on success
            if(response.data.success) {
                setCars((prevCars) =>
                    prevCars.map((car) =>
                        car.id === id ? {...car, isActive: newValue} : car
                    )
                );
            }
        } catch (error) {
            console.error('Error updating car status:', error);
        }
    };
    const handleLogOutClick = () => {
        const AUTH_URL = process.env.REACT_APP_AUTH_URL;
        // Clear the dashboard's local session state first.
        Object.keys(Cookies.get()).forEach(cookieName => {
            Cookies.remove(cookieName);
        });
        localStorage.clear();
        dispatch(setLogout());
        // Top-level redirect to the auth server logout: a GET navigation sends the SRLSESSION
        // cookie so the SSO session is actually invalidated (a cross-origin XHR cannot), then the
        // auth server redirects back to the dashboard, which re-initiates a fresh login.
        window.location.href = `${AUTH_URL}/logout`;
    };
    const saveNewCars = async (e)=>{
        e.preventDefault();
        if(!newName.trim()){
            setError("Car Name is required");
            return;
        }
        setLoading(true);
        try {
            const response = await axiosInstance.post(`${BASE_URL}/srl/master-cars/save`,{
                "country":currentCountryName,
                "countryCode":selectedCountry,
                "carName":newName,
                "isActive":newActive ? 1:0
            });
            closeModal();
        }catch (error){
            console.error('Error updating car status:', error);
        }
        finally {
            setLoading(false);
        }
    }

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    const currentCountryName = countries.find(country =>country.country === selectedCountry)?.countryName || selectedCountry;

    return (
        <div style={{ padding: '24px' }}>
            <div className="dashboard-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Settings</h1>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <Navbar currentPage="settings" onLogout={handleLogOutClick} />
                </div>
            </div>

            {/* Country Dropdown */}
            <div className="filter-input-group">
                <select
                    value={selectedCountry}
                    onChange={(e) => setSelectedCountry(e.target.value)}
                    className="filter-input"
                >
                    <option value="">All Countries</option>
                    {countries.map((c) => (
                        <option key={c.country} value={c.country}>
                            {c.countryName}
                        </option>
                    ))}
                </select>
            </div>

            {/* Cars List */}
            {selectedCountry && (
                <div style={{ marginTop: '32px' }}>
                    <div
                        style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            marginBottom: '8px',
                        }}
                        >
                    <h2 style={{ fontSize: '18px', fontWeight: '500', margin: 0 }}>Cars</h2>
                    <button
                        onClick={openModal}
                        style={{
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '4px',
                            padding: '6px 12px',
                            fontSize: '14px',
                            fontWeight: 500,
                            borderRadius: '6px',
                            border: '1px solid #3b82f6',
                            backgroundColor: '#3b82f6',
                            color: '#fff',
                            cursor: 'pointer',
                        }}
                        aria-label="Add car"
                    >
                        + Add Car
                    </button>
                </div>
                    {
                        cars.length === 0 ? (
                        <p style={{ color: '#6b7280' }}>No cars found for selected country.</p>
                    ) :
                        (
                            <ul style={{ listStyle: 'none', padding: 0 }}>
                                {cars.map((car) => (
                                    <li
                                        key={car.id}
                                        style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',
                                            padding: '12px 16px',
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '6px',
                                            marginBottom: '12px',
                                            backgroundColor: '#f9fafb',
                                        }}
                                    >
                                        <span style={{ fontSize: '15px', fontWeight: 500 }}>{car.carName}</span>
                                        <label className="switch">
                                            <input
                                                type="checkbox"
                                                checked={car.isActive}
                                                onChange={(e) => handleToggle(car.id,car.carName, e.target.checked)}
                                            />
                                            <span className="slider round"></span>
                                        </label>
                                    </li>
                                ))}
                            </ul>
                        )
                    }
                </div>
            )}
            {/* Modal */}
            {isModalOpen &&
                (
                <div
                    role="dialog"
                    aria-modal="true"
                    style={{
                        position: 'fixed',
                        inset: 0,
                        backgroundColor: 'rgba(0,0,0,0.4)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000,
                        padding: '16px',
                    }}
                    onClick={(e) => {
                        if (e.target === e.currentTarget) closeModal();
                    }}
                >
                    <div
                        style={{
                            background: '#fff',
                            borderRadius: '10px',
                            width: '100%',
                            maxWidth: '420px',
                            padding: '24px',
                            boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
                            position: 'relative',
                        }}
                    >
                        <h3 style={{ marginTop: 0, marginBottom: '12px', fontSize: '18px' }}>
                            Add New Car
                        </h3>
                        <form onSubmit={saveNewCars}>
                            <div style={{ marginBottom: '12px' }}>
                                <label
                                    htmlFor="countryDisplay"
                                    style={{ display: 'block', fontSize: '14px', marginBottom: '4px' }}
                                >
                                    Country
                                </label>
                                <input
                                    id="countryDisplay"
                                    type="text"
                                    value={currentCountryName}
                                    readOnly
                                    style={{
                                        width: '100%',
                                        padding: '8px 10px',
                                        fontSize: '14px',
                                        borderRadius: '6px',
                                        border: '1px solid #d1d5db',
                                        boxSizing: 'border-box',
                                        backgroundColor: '#f3f4f6',
                                        cursor: 'not-allowed',
                                    }}
                                />
                            </div>
                            <div style={{ marginBottom: '12px' }}>
                                <label
                                    htmlFor="carName"
                                    style={{ display: 'block', fontSize: '14px', marginBottom: '4px' }}
                                >
                                    Car Name
                                </label>
                                <input
                                    id="carName"
                                    onChange={(e) => {
                                        setNewName(e.target.value);
                                        if (error) setError(null);
                                    }}
                                    type="text"
                                    value={newName}
                                    style={{
                                        width: '100%',
                                        padding: '8px 10px',
                                        fontSize: '14px',
                                        borderRadius: '6px',
                                        border: '1px solid #d1d5db',
                                        boxSizing: 'border-box',
                                    }}
                                    placeholder="Enter car name"
                                    aria-invalid={!!error}
                                />
                            </div>
                            <div
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '8px',
                                    marginBottom: '16px',
                                }}
                            >
                                <input
                                    id="isActive"
                                    type="checkbox"
                                    checked={newActive}
                                    style={{ width: '16px', height: '16px' }}
                                    onChange={(e) => setNewActive(e.target.checked)}
                                />
                                <label htmlFor="isActive" style={{ fontSize: '14px' }}>
                                    Active
                                </label>
                            </div>
                            {error && (
                                <div
                                    style={{
                                        color: '#b91c1c',
                                        fontSize: '13px',
                                        marginBottom: '12px',
                                    }}
                                >
                                    {error}
                                </div>
                            )}
                            <div
                                style={{
                                    display: 'flex',
                                    justifyContent: 'flex-end',
                                    gap: '8px',
                                    marginTop: '4px',
                                }}
                            >
                                <button
                                    type="button"
                                    onClick={closeModal}
                                    style={{
                                        padding: '8px 14px',
                                        fontSize: '14px',
                                        borderRadius: '6px',
                                        border: '1px solid #d1d5db',
                                        backgroundColor: '#f3f4f6',
                                        cursor: 'pointer',
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    style={{
                                        padding: '8px 14px',
                                        fontSize: '14px',
                                        borderRadius: '6px',
                                        border: 'none',
                                        backgroundColor: '#2563eb',
                                        color: '#fff',
                                        cursor: 'pointer',
                                    }}
                                >
                                    Add Car
                                </button>
                            </div>
                        </form>
                        <button
                            aria-label="Close"
                            onClick={closeModal}
                            style={{
                                position: 'absolute',
                                top: '10px',
                                right: '10px',
                                background: 'transparent',
                                border: 'none',
                                fontSize: '16px',
                                cursor: 'pointer',
                            }}
                        >
                            ×
                        </button>
                    </div>
                </div>)}
        </div>
    );
};

export default Settings;
