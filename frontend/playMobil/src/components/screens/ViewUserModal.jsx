import React, { useState } from "react";

const ViewUserModal = ({ user, onClose }) => {
    const [visibleCount, setVisibleCount] = useState(5);

    if (!user) return null;

    const userCountries = user.userCountries || [];

    const handleShowMore = () => {
        setVisibleCount(prev => Math.min(prev + 10, userCountries.length));
    };

    const handleSeeLess = () => {
        setVisibleCount(5);
    };

    return (
        <div
            className="modal fade show d-block"
            tabIndex="-1"
            role="dialog"
            style={{ background: "rgba(0,0,0,0.5)" }}
        >
            <div
                className="modal-dialog modal-lg modal-dialog-centered"
                style={{ maxHeight: "90vh", overflowY: "auto" }}
            >
                <div className="modal-content shadow-lg">
                    <div className="modal-header">
                        <h5 className="modal-title">
                            <i className="bi bi-eye me-2"></i> View User
                        </h5>
                        <button
                            type="button"
                            className="btn-close"
                            onClick={onClose}
                        ></button>
                    </div>
                    <div className="modal-body">
                        <form className="row g-3">
                            <div className="col-md-6 d-flex flex-column align-items-start">
                                <label className="form-label mb-1">First Name</label>
                                <div className="form-control text-start">{user.firstName}</div>
                            </div>
                            <div className="col-md-6 d-flex flex-column align-items-start">
                                <label className="form-label mb-1">Last Name</label>
                                <div className="form-control text-start">{user.lastName}</div>
                            </div>
                            <div className="col-md-6 d-flex flex-column align-items-start">
                                <label className="form-label mb-1">User Name</label>
                                <div className="form-control text-start">{user.userName}</div>
                            </div>
                            <div className="col-md-6 d-flex flex-column align-items-start">
                                <label className="form-label mb-1">Email address</label>
                                <div className="form-control text-start">{user.emailAddress}</div>
                            </div>
                            <div className="col-md-6 d-flex flex-column align-items-start">
                                <label className="form-label mb-1">Role</label>
                                <div className="form-control text-start">
                                    {user.roles && user.roles.length > 0 ? user.roles[0].name : "No Role"}
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ViewUserModal;

