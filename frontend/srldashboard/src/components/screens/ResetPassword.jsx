import React, { useState } from "react";
import { useLocation,useNavigate } from "react-router-dom";
import { Lock, User, Eye, EyeOff } from "lucide-react";
import axiosInstance from "../../interceptors";

const ResetPassword = ({ onSubmit, loading = false, maxWidth = 640 }) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showPwd, setShowPwd] = useState(false);
    const [showConfirmPwd, setShowConfirmPwd] = useState(false);
    const [errors, setErrors] = useState({});
    const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;

    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const id = Number(searchParams.get("id"));
    const navigate = useNavigate();

    const validate = () => {
        const newErrors = {};
        if (!username.trim()) newErrors.username = "Username is required";
        if (!password.trim()) newErrors.password = "Password is required";
        else if (password.length < 6) newErrors.password = "Password must be at least 6 characters";
        if (password !== confirmPassword) newErrors.confirmPassword = "Passwords do not match";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validate()) return;
        onSubmit?.({ username, password });
        try {
            const response = await axiosInstance.post(`${BASE_URL}/user/reset-new-password`,{
                "id":id,
                "newPassword":password
            });
            if(response.status === 200) {
                navigate("/signin");
            }
        }catch (error){
            console.error('Error updating car status:', error);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
            <div className="card shadow-lg w-100" style={{ maxWidth }}>
                <div className="card-body p-4 p-md-5">
                    <div className="d-flex align-items-center justify-content-center mb-4">
                        <Lock size={24} className="me-2" />
                        <h3 className="m-0">Reset Password</h3>
                    </div>

                    <form onSubmit={handleSubmit} noValidate>
                        {/* Username */}
                        <div className="mb-3">
                            <label className="form-label fw-semibold">Username</label>
                            <div className="input-group">
                <span className="input-group-text">
                  <User size={18} />
                </span>
                                <input
                                    type="text"
                                    className={`form-control ${errors.username ? "is-invalid" : ""}`}
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    autoComplete="blabla"
                                    placeholder="Enter your username"

                                />
                                {!username ? (errors.username && <div className="invalid-feedback">{errors.username}</div>) :''}
                            </div>
                        </div>

                        {/* New Password */}
                        <div className="mb-3">
                            <label className="form-label fw-semibold">New Password</label>
                            <div className="input-group">
                <span className="input-group-text">
                  <Lock size={18} />
                </span>
                                <input
                                    type={showPwd ? "text" : "password"}
                                    className={`form-control ${errors.password ? "is-invalid" : ""}`}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    autoComplete="new-password"
                                    placeholder="Enter a strong password"
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={() => setShowPwd((prev) => !prev)}
                                    tabIndex={-1}
                                >
                                    {showPwd ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            {!password ? (errors.password && <div className="invalid-feedback d-block">{errors.password}</div>):''}
                        </div>

                        {/* Confirm Password */}
                        <div className="mb-4">
                            <label className="form-label fw-semibold">Confirm Password</label>
                            <div className="input-group">
                <span className="input-group-text">
                  <Lock size={18} />
                </span>
                                <input
                                    type={showConfirmPwd ? "text" : "password"}
                                    className={`form-control ${errors.confirmPassword ? "is-invalid" : ""}`}
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    autoComplete="new-password"
                                    placeholder="Re-enter new password"
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={() => setShowConfirmPwd((prev) => !prev)}
                                    tabIndex={-1}
                                >
                                    {showConfirmPwd ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            { !confirmPassword ?<div className="invalid-feedback d-block">Password is required</div>:(errors.confirmPassword && (
                                <div className="invalid-feedback d-block">{errors.confirmPassword}</div>
                            ))}
                        </div>

                        <button type="submit" className="btn btn-primary w-100 py-2" disabled={loading}>
                            {loading ? "Updating..." : "Reset Password"}
                        </button>
                    </form>

                    <p className="text-muted mt-3 mb-0" style={{ fontSize: 12 }}>
                        Password must be at least 6 characters. Consider using a mix of letters, numbers, and symbols.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ResetPassword;
