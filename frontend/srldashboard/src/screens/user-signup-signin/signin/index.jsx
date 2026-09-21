import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import Cookies from "js-cookie";
import { useEffect } from "react";
import EvuemeLoader from "../../../components/loaders/evueme-loader";
import { OAUTH } from "../../../config/config";

const SigninPage = () => {
  const navigate = useNavigate();

  const { accessToken } = useSelector(
      (state) => state.signinSliceReducer
  );

  useEffect(() => {
    const token =
      localStorage.getItem("e_access_token") ||
      Cookies.get("e_access_token") ||
      accessToken;

    if (!token) {
      // Always start login via the OAuth2 authorization endpoint (same as the first
      // login). This lets the auth server save the authorization request and, after a
      // successful login, redirect back through /oauth2/callback to the dashboard.
      // Sending the user straight to /login after logout left them stranded on the
      // login page, because that path has no saved authorization request to return to.
      const authBase = process.env.REACT_APP_AUTH_URL+"/oauth2/authorize";
      const params = new URLSearchParams();
      params.append("redirect_uri", OAUTH.REDIRECT_URI);
      params.append("response_type", "code");
      params.append("client_id", OAUTH.CLIENT_ID);
      params.append("scope", "openid");

      let targetUrl = `${authBase}?${params.toString()}`;

      window.location.href = targetUrl;
    } else {
      navigate("/");
    }
  }, [accessToken]);

  return <EvuemeLoader />;

  // return (
  //   <div className="container full-height valign-wrapper row-relative">
  //     <div className="row">
  //       <UserSignupSinginPageImage />
  //       <Siginform />
  //     </div>
  //   </div>
  // );
};

export default SigninPage;
