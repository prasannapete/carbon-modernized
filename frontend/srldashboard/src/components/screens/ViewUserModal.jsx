import React, { useState } from "react";

const ViewUserModal = ({ user, onClose }) => {
    const [visibleCount, setVisibleCount] = useState(5);

    if (!user) return null;

    const handleShowMore = () => {
        setVisibleCount(prev => Math.min(prev + 10, user.userCountries.length));
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
                                <label className="form-label mb-1">Role </label>
                                <div className="form-control text-start">{user.isGod ? "Admin" : "User"}</div>
                            </div>
                        </form>

                        <div className="mt-3">
                            <div className="col-md-12 d-flex flex-column align-items-start">
                                <label className="form-label">Selected Countries</label>
                                <div className="d-flex flex-wrap align-items-center">
                                    {user.userCountries.length > 0 ? (
                                        <>
                                            {user.userCountries
                                                .slice(0, visibleCount)
                                                .map((c, i) => (
                                                    <span
                                                        key={i}
                                                        className="badge bg-secondary me-1 mb-1"
                                                    >
                                                        {c.countryName}
                                                    </span>
                                                ))}

                                            {visibleCount < user.userCountries.length ? (
                                                <button
                                                    type="button"
                                                    className="badge rounded-pill bg-info text-dark mb-1 border-0"
                                                    onClick={handleShowMore}
                                                >
                                                    +{Math.min(
                                                    10,
                                                    user.userCountries.length - visibleCount
                                                )} more
                                                </button>
                                            ) : (
                                                user.userCountries.length > 5 && (
                                                    <button
                                                        type="button"
                                                        className="badge rounded-pill bg-danger  mb-1 border-0"
                                                        onClick={handleSeeLess}
                                                    >
                                                        See Less
                                                    </button>
                                                )
                                            )}
                                        </>
                                    ) : (
                                        <span className="text-muted">None</span>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ViewUserModal;
