import React, {useEffect, useState} from "react";
import {Globe, Mail, User, Users,ShieldUser} from "lucide-react";
import ViewUserModal from "./ViewUserModal";
import axiosInstance from "../../interceptors";
import {useNavigate} from 'react-router-dom';
import {useDispatch} from "react-redux";

import Navbar from "./Navbar";
import '../../css/Dashboard.css';
import Cookies from "js-cookie";
import {setLogout} from "../../redux/slices/signin-slice";
import CountrySelector from "../selector/CountrySelector";


const UserForm = () => {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [selectedCountries, setSelectedCountries] = useState([]);
    const [countries, setCountries] = useState([]);
    const [username, setUsername] = useState("");
    const [isGod, setIsGod] = useState(0);
    const [errors, setErrors] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [users, setUsers] = useState([]);
    const [editingUserId, setEditingUserId] = useState(null);
    const [viewingUser, setViewingUser] = useState(null);
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    const dispatch = useDispatch();
    const navigate = useNavigate();


    const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;


    useEffect(() => {

        const fetchCountries = async () => {
            try {
                const response = await axiosInstance.post(`${BASE_URL}/srl/country/get-countries`);
                const defaultOptions = {
                    value: "all",
                    label: "All country",
                }
                const countryOptions = response.data.data.map((c) => ({
                    value: c.id,
                    label: c.countryName,
                }));
                let options = [defaultOptions, ...countryOptions];
                setCountries(options);
            } catch (error) {
                console.error('Failed to fetch countries:', error);
            }
        };

        fetchCountries();
    }, []);
    const fetchUsers = async () => {
        try {
            const payload = {current_page: String(pageNumber),
                page_size: String(pageSize),
                sort_field: "creationTime",
                sort_order: "asc",
                search_text: ""}
            const response = await  axiosInstance.post(`${BASE_URL}/user/search-paginated`, payload);
            const newUsers = response.data.data || [];
            setUsers(prev => [...prev, ...newUsers]);
            setPageNumber(prev => prev + 1);
            // If fewer records returned than pageSize, stop further fetch
            if (newUsers.length < pageSize) setHasMore(false);
        }
        catch (error) {
            console.error("Failed to fetch users",error);
        }
    };
    useEffect(() => {

        fetchUsers();
    },[]);
    // Auto-generate username
    useEffect(() => {
        if(!editingUserId) {
            if (firstName || lastName) {
                setUsername(
                    `${firstName}.${lastName}`.toLowerCase().replace(/\s+/g, "")
                );
            } else {
                setUsername("");
            }
        }
    }, [firstName, lastName,editingUserId]);
    const handleLogOutClick = async () => {
        const AUTH_URL = process.env.REACT_APP_AUTH_URL;
        try {
            // Call the logout API endpoint
            await axiosInstance.post(`${AUTH_URL}/logout`);
        } catch (error) {
            console.error('Logout API call failed:', error);
        } finally {
            Object.keys(Cookies.get()).forEach(cookieName => {
                Cookies.remove(cookieName);
            });
            localStorage.clear();
            dispatch(setLogout());
            console.log("Redirecting to sign in page!");
            navigate('/signin', { replace: true });
        }
    };


    // Form validation
    const validate = () => {
        const newErrors = {};
        if (!firstName.trim()) newErrors.firstName = "First name is required";
        if (!lastName.trim()) newErrors.lastName = "Last name is required";
        if (!email.trim()) {
            newErrors.email = "Email is required";
        } else if (
            !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email.trim())
        ) {
            newErrors.email = "Invalid email address";
        }
        if (selectedCountries.length === 0)
            newErrors.countries = "Please select at least one country";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validate()) return;
        setLoading(true);

        const formData = {
            firstName,
            lastName,
            emailAddress:email,
            countryIds: selectedCountries.map((c) => c.value).join(','),
            userName:username,
            id:editingUserId || null,
            isGod
    };
        try {
            const response = await axiosInstance.post(`${BASE_URL}/user/save-user`,formData);
            setShowModal(false);
            setShowModal(false);
            setFirstName("");
            setLastName("");
            setUsername("");
            setEmail("");
            setSelectedCountries([]);
            setIsGod(0);
            window.location.reload();
        }catch (error){
            console.error('Error updating car status:', error);
        }
        finally {
            setLoading(false);
        }

    };

    const handleClose = () => {
        setShowModal(false);
        setFirstName("");
        setLastName("");
        setUsername("");
        setEmail("");
        setSelectedCountries([]);
        setErrors({});
        setIsGod(0);
        setEditingUserId(null);


    }
    const handleEdit = (user) => {
        setFirstName(user.firstName);
        setLastName(user.lastName);
        setEmail(user.emailAddress);
        setSelectedCountries(user.userCountries.map(c => ({ value: c.countryId, label: c.countryName })));
        setUsername(user.userName);
        setShowModal(true);
        setEditingUserId(user.id); // keep track of which user is being edited
        setIsGod(user.isGod);

    };

// Delete handler
    const handleDelete = async (id) => {
        try {
            const response = await axiosInstance.delete(`${BASE_URL}/user/delete/${id}`);
            if (response.data.success){
                window.location.reload();

            }

        }
        catch (error) {
            console.error('Failed to delete user', error);
        }
    };

    return (
        <div style={{ padding: '24px' }}>
            <div className="header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Users List</h1>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <Navbar currentPage="users" onLogout={handleLogOutClick}/>
                </div>
            </div>
        <div className="container my-5 text-center">
            <div
                style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '8px',
                }}
            >
                <h2 style={{ fontSize: '18px', fontWeight: '500', margin: 0 }}>
                    <Users className="w-10 h-10" aria-label="Users" /> Users</h2>
            <button
                style={{display: 'inline-flex',
                    alignItems: 'center',
                    gap: '4px',
                    padding: '6px 12px',
                    fontSize: '14px',
                    fontWeight: 500,
                    borderRadius: '6px',
                    border: '1px solid #3b82f6',
                    backgroundColor: '#3b82f6',
                    color: '#fff',
                    cursor: 'pointer',}}
                onClick={() => setShowModal(true)}
            >
                + Add User
            </button>
            </div>
            <div className="card shadow-sm">
                <div className="card-body table-responsive">
                    <table className="table align-middle table-hover">
                        <thead className="table-light">
                        <tr>
                            <th>User Name</th>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Email</th>
                            <th>Action</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="text-center text-muted">
                                    No users added yet.
                                </td>
                            </tr>
                        ) : (
                            users.map((user) => (
                                <tr key={user.id}>
                                    <td>
                                        <span className="fw-semibold">
                                            {user.userName}
                                        </span>
                                    </td>
                                    <td>{user.firstName}</td>
                                    <td>{user.lastName}</td>
                                    <td>{user.emailAddress}</td>
                                    <td>
                                        {/* Action buttons */}
                                        <button
                                            className="btn btn-sm btn-outline-info me-2"
                                            onClick={() => setViewingUser(user)}
                                        >
                                            <i className="bi bi-eye"></i> View
                                        </button>
                                        <button
                                            className="btn btn-sm btn-outline-primary me-2"
                                            onClick={() => handleEdit(user)}
                                        >
                                            <i className="bi bi-pencil-square"></i> Edit
                                        </button>
                                        <button
                                            className="btn btn-sm btn-outline-danger"
                                            onClick={() => handleDelete(user.id)}
                                        >
                                            <i className="bi bi-trash"></i> Delete
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                    {hasMore && (
                        <div className="text-center mt-3">
                            <button className="btn btn-primary" onClick={fetchUsers} disabled={loading}>
                                {loading ? "Loading..." : "Load More"}
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {/* Modal */}
            {showModal && (
                <div
                    className="modal fade show d-block"
                    tabIndex="-1"
                    role="dialog"
                    style={{ background: "rgba(0,0,0,0.5)" }}
                >
                    <div className="modal-dialog modal-dialog-centered" role="document">
                        <div className="modal-content shadow-lg">
                            <div className="modal-header">
                                <h5 className="modal-title d-flex align-items-center gap-2">
                                    <User size={20} /> {editingUserId ? "Edit User" : "Add User"}
                                </h5>
                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={() => handleClose()}
                                ></button>
                            </div>
                            <div className="modal-body">
                                <form onSubmit={handleSubmit} className="row g-3">
                                    {/* First Name */}
                                    <div className="col-md-6">
                                        <label className="form-label  d-flex align-items-center gap-1">First Name</label>
                                        <input
                                            type="text"
                                            className={`form-control ${
                                               !firstName ? (errors.firstName ? "is-invalid" : ""):""
                                            }`}
                                            value={firstName}
                                            onChange={(e) => setFirstName(e.target.value)}
                                            placeholder="Enter first name"
                                            autoComplete="bla-bla"
                                        />
                                        {errors.firstName && (
                                            <div className="invalid-feedback">{errors.firstName}</div>
                                        )}
                                    </div>

                                    {/* Last Name */}
                                    <div className="col-md-6">
                                        <label className="form-label  d-flex align-items-center gap-1">Last Name</label>
                                        <input
                                            type="text"
                                            className={`form-control ${
                                                !lastName ?(errors.lastName ? "is-invalid" : ""):""
                                            }`}
                                            value={lastName}
                                            onChange={(e) => setLastName(e.target.value)}
                                            placeholder="Enter last name"
                                            autoComplete="bla-bla"

                                        />
                                        {errors.lastName && (
                                            <div className="invalid-feedback">{errors.lastName}</div>
                                        )}
                                    </div>

                                    {/* Email */}
                                    <div className="col-12">
                                        <label className="form-label d-flex align-items-center gap-1">
                                            <Mail size={16} /> Email
                                        </label>
                                        <input
                                            type="email"
                                            className={`form-control ${
                                                !email?(errors.email ? "is-invalid" : ""):""
                                            }`}
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            placeholder="example@email.com"
                                            autoComplete="bla-bla"

                                        />
                                        {errors.email && (
                                            <div className="invalid-feedback">{errors.email}</div>
                                        )}
                                    </div>

                                    {/* Username */}
                                    <div className="col-12">
                                        <label className="form-label  d-flex align-items-center gap-1">Username</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={username}
                                            onChange={(e) => setUsername(e.target.value)}
                                            readOnly={!!editingUserId}
                                        />
                                    </div>
                                    {/* Admin Toggle */}
                                    <div className="col-12 d-flex align-items-center gap-2 mt-2">
                                        <ShieldUser size={20} color={isGod ? "#2563eb" : "#6b7280"} />
                                        <label className="form-label mb-0">Is Admin</label>
                                        <input
                                            type="checkbox"
                                            className="form-check-input"
                                            checked={isGod === 1}
                                            onChange={(e) => setIsGod(e.target.checked ? 1 : 0)}
                                            style={{ cursor: "pointer" }}
                                        />
                                    </div>


                                    {/* Visible Countries */}
                                    <div className="col-12">
                                        <label className="form-label d-flex align-items-center gap-1">
                                            <Globe size={16} /> Visible Countries
                                        </label>
                                        {/*<CountrySelector*/}
                                        {/*    isMulti*/}
                                        {/*    value={selectedCountries}*/}
                                        {/*    onChange={setSelectedCountries}*/}
                                        {/*    data={countries}*/}
                                        {/*    placeholder="Select Countries"*/}
                                        {/*    classNamePrefix="select"*/}
                                        {/*/>*/}
                                        <CountrySelector
                                            isMulti
                                            value={selectedCountries.map(c => c.value)}
                                            onChange={(ids) => {
                                                const selected = ids.map(id => {
                                                    const country = countries.find(c => c.value === id);
                                                    return { value: country.value, label: country.label };
                                                });
                                                setSelectedCountries(selected);
                                            }}
                                            data={countries}
                                            placeholder="Select Countries"
                                            classNamePrefix="select"
                                        />

                                        {! (selectedCountries.length >0) ?(errors.countries && (
                                            <div className="text-danger mt-1">
                                                {errors.countries}
                                            </div>
                                        )):""}
                                    </div>

                                    {/* Submit Button */}
                                    <div className="col-12">
                                        <button type="submit" className="btn btn-primary w-100">
                                            Submit
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            <ViewUserModal user={viewingUser} onClose={() => setViewingUser(null)} />
        </div>
        </div>
    );
};

export default UserForm;
