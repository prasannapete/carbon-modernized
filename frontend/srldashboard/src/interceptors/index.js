// axiosInstance.js
import axios from "axios";
import Cookies from "js-cookie";
import { setLogout } from "../redux/slices/signin-slice";
import store from "../redux/store/store";

const axiosInstance = axios.create();

// Add a request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const accessToken =
      localStorage.getItem("e_access_token") || Cookies.get("e_access_token");

  // Add the Authorization header if the access token is available
    if (accessToken) {
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle 401 errors
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      store.dispatch(setLogout());
      window.location.href = "/signin";
      return Promise.reject({
        status: 401,
        message: "Session expired. Please login again.",
      });
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;