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
    const authUrl = (OAUTH.AUTH_URL || "").trim();
    const redirectUri = (OAUTH.REDIRECT_URI || `${window.location.origin}/oauth2/callback`).trim();
    const token =
      localStorage.getItem("e_access_token") ||
      Cookies.get("e_access_token") ||
      accessToken;

    if (!token) {
      if (!authUrl) {
        // Prevent redirect loops like /undefined/oauth2/authorize when env is missing.
        return;
      }

      // Always start login via the OAuth2 authorization endpoint (same as the first login),
      // including after logout. Sending the user straight to /login after logout leaves them
      // stranded (that path has no saved authorization request to return through
      // /oauth2/callback), which showed up as /login;SRLSESSION=... with a non-working form.
      const authBase = `${authUrl}/oauth2/authorize`;
      const params = new URLSearchParams();
      params.append("redirect_uri", redirectUri);
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
