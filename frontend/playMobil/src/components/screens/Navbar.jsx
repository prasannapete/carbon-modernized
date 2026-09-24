import React, {useEffect, useState} from "react";
import { Link } from "react-router-dom";
import { Users, Settings, LogOut,LayoutDashboard } from "lucide-react";
import '../../css/Dashboard.css';
import axiosInstance from "../../interceptors";


const Navbar = ({ currentPage, onLogout }) => {

    const [isGod,setIsGod] = useState(null);

    const BASE_URL = process.env.REACT_APP_SHELL_ANALYTICS_SERVICE_BASE_URL;


    useEffect(() => {
        const profile = async ()=>{
            try {
                const response = await axiosInstance.post(`${BASE_URL}/user/get-my-profile`);
                setIsGod(response?.data?.user?.isGod ?? null);
            } catch (e) {
                setIsGod(null);
            }
        }
        profile();
    },[])

    const actions = [
        { key: "users", type: "link", to: "/users", icon: Users, iconClass: "w-10 h-10", label: null, className: "action-link" },
        { key: "logout", type: "button", icon: LogOut, iconClass: "w-4 h-4", label: "Logout", className: "action-link logout-variant" },

    ];
    // Exclude the current page
    const filteredActions = actions.filter(action => {
        if (action.key === "users" && isGod !== 1) return false; // only show Users if isGod === 1
        return action.key !== currentPage;
    });
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
