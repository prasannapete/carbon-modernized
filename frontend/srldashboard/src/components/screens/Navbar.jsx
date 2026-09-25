import React from "react";
import { Link } from "react-router-dom";
import { Users, Settings, LogOut,LayoutDashboard } from "lucide-react";
import '../../css/Dashboard.css';


const Navbar = ({ currentPage, onLogout }) => {

    const actions = [
        { key: "users", type: "link", to: "/users", icon: Users, iconClass: "w-10 h-10", label: null, className: "action-link" },
        { key: "settings", type: "link", to: "/settings", icon: Settings, iconClass: "w-4 h-4", label: "Settings", className: "action-link" },
        { key: "dashboard", type: "link", to: "/*", icon:LayoutDashboard,iconClass: "w-4 h-4", label: "Dashboard", className: "action-link",},
        { key: "logout", type: "button", icon: LogOut, iconClass: "w-4 h-4", label: "Logout", className: "action-link logout-variant" },

    ];
    // Show every action (the Users icon is available to all logged-in users), except the
    // one for the page currently being viewed.
    const filteredActions = actions.filter(action => action.key !== currentPage);
    return (
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
            {filteredActions.map((action, index) => {
                if (action.type === "link") {
                    return (
                        <Link key={index} to={action.to} className={action.className}>
                            {action.icon && <action.icon className={action.iconClass} aria-label={action.label || ""} />}
                            {action.label && <span>{action.label}</span>}
                        </Link>
                    );
                } else if (action.type === "button") {
                    return (
                        <button
                            key={index}
                            className={action.className}
                            aria-label={action.label}
                            onClick={onLogout}
                        >
                            {action.icon && <action.icon className={action.iconClass} />}
                            {action.label && <span>{action.label}</span>}
                        </button>
                    );
                } else if (action.type === "custom") {
                    return (
                        <Link key={index} to={action.to} className={action.className}>
                            {action.customIcon}
                            {action.label && <span>{action.label}</span>}
                        </Link>
                    );
                }
                return null;
            })}
        </div>
    );
};

export default Navbar;
