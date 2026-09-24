import axios, { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { OAUTH } from "../../config/config";
import Cookies from "js-cookie";
import { parseJwt } from "../../utils/parseJwt";
import { setUserState, resetLogoutFlag } from "../../redux/slices/signin-slice";
import { useDispatch } from "react-redux";
import EvuemeLoader from "../loaders/evueme-loader";

// Guards against exchanging the same one-time authorization code more than once
// (e.g. a component re-mount / double effect). A second exchange of the same code
// triggers the auth server's code-reuse protection and revokes the issued token.
const exchangedCodes = new Set();

const AuthCallback = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const dispatch = useDispatch();

  useEffect(() => {
    const auth = async () => {
      setLoading(true);
      const authUrl = (OAUTH.AUTH_URL || "").trim();
      const redirectUri = (OAUTH.REDIRECT_URI || `${window.location.origin}/oauth2/callback`).trim();

      // Run on auth callback
      let url = new URL(window.location.href);
      let code = url.searchParams.get("code");
      if (!code) {
        return navigate("/", { replace: true });
      }
      // Exchange each authorization code only once.
      if (exchangedCodes.has(code)) {
        return;
      }
      exchangedCodes.add(code);

      let data = new URLSearchParams();
      data.append("grant_type", OAUTH.GRANT_TYPE);
      data.append("code", code);
      data.append("redirect_uri", redirectUri);

      // Authorization header
      const basicAuth = btoa(`${OAUTH.CLIENT_ID}:${OAUTH.CLIENT_SECRET}`);
      const contenType = "application/x-www-form-urlencoded";
      try {
        if (!authUrl) {
          return navigate("/signin", { replace: true });
        }
        let res = await axios.post(
            `${authUrl}/oauth2/token`,
          data.toString(),
          {
            headers: {
              "Content-Type": contenType,
              Authorization: `Basic ${basicAuth}`,
            },
          }
        );

        if (res.status !== 200) {
          console.error("code <-> token failed:", res.status, res.data);
          return navigate("/", { replace: true });
        }

        let accessToken = res.data.access_token;
        let { userinfo } = parseJwt(accessToken);
        dispatch(
            setUserState({
              userName: `${userinfo?.firstName}${userinfo?.lastName ? ' ' + userinfo.lastName : ''}`,
              tncStatus: userinfo?.tncStatus,
              userId: userinfo?.id,
              userType: "",
              token: accessToken,
            })
        );
        // Reset logout flag on successful authentication
        dispatch(resetLogoutFlag());

        Cookies.set("e_access_token", accessToken, {
          expires: 1,
        });


        navigate("/", { replace: true });
      } catch (error) {
        if (error instanceof AxiosError) {
          console.error("AxiosError code <-> token:", error);
        }
        console.error("Unknown error code <-> token:", error);
      } finally {
        setLoading(false);
      }
    };

    auth();
  }, []);

  return loading ? <EvuemeLoader /> : null;
};

export default AuthCallback;
