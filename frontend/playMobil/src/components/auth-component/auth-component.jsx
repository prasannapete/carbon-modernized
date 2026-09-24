import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
// import EvuemeLoader from "../loaders/evueme-loader";
import { useSelector } from "react-redux";
import ErrorToast from "../toasts/error-toast";
import Cookies from "js-cookie";

const AuthComponent = ({ children }) => {
  const { accessToken, tncStatus, userType } = useSelector(
    (state) => state.signinSliceReducer
  );
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    try {
      setLoading(true);
      const token =
        localStorage.getItem("e_access_token") ||
        accessToken;

      if (!token) {
        navigate("/signin", { replace: true });
      }
      if (tncStatus === false) {
        navigate("/admin/terms-of-use");
      }
    } catch (error) {
      ErrorToast(error.message);
      navigate("/signin", { replace: true });
    } finally {
      setLoading(false);
    }
  }, [accessToken, navigate]);

  return !loading ? <>{children}</> :null;
};

export default AuthComponent;
