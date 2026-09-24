import { createSlice } from "@reduxjs/toolkit";
import Cookies from "js-cookie";

const initialState = {
  currentUser: localStorage.getItem("currentUser") || "",
  accessToken: localStorage.getItem("e_access_token") ||null,
  tncStatus: localStorage.getItem("tncStatus") || null,
  userId: localStorage.getItem("userId") || null,
  userType: localStorage.getItem("userType") || null,
  isLogout: false,
};

const signinSlice = createSlice({
  name: "signinSlice",
  initialState,
  reducers: {
    setUserState: (state, action) => {
      const userType = (action.payload.userType || "").toLowerCase();
      state.currentUser = action.payload.userName;
      state.userType = userType;
      state.tncStatus = action.payload.tncStatus;
      state.userId = action.payload.userId;
      state.accessToken = action.payload.token;
      state.isLogout= false;

      localStorage.setItem("currentUser", action.payload.userName);
      localStorage.setItem("tncStatus", action.payload.tncStatus);
      localStorage.setItem("userId", action.payload.userId);
      localStorage.setItem("userType", userType);
      localStorage.setItem("e_access_token", action.payload.token);
    },
    setCurrentUser: (state, action) => {
      state.currentUser = action.payload;
      localStorage.setItem("currentUser", action.payload);
    },
    setAccessToken: (state, action) => {
      state.accessToken = action.payload;
      localStorage.setItem("e_access_token", action.payload);
    },
    setTnCStatus: (state, action) => {
      state.userId = action.payload.userId;
      state.tncStatus = action.payload.status;
      localStorage.setItem("tncStatus", action.payload.status);
      localStorage.setItem("userId", action.payload.userId);
    },
    setLogout: (state, action) => {
      Cookies.remove("e_access_token");
      const selectedCountries = localStorage.getItem("selectedCountries");
      localStorage.clear();
      // Restore selectedCountries
      if (selectedCountries) {
        localStorage.setItem("selectedCountries", selectedCountries);
      }
      state.userType = "";
      state.accessToken = null;
      state.currentUser = "";
      state.tncStatus = null;
      state.userId = null;
      state.isLogout = true;
    },
    resetLogoutFlag: (state, action) => {
      state.isLogout = false;
    }
  },
});

export const {
  setCurrentUser,
  setAccessToken,
  setTnCStatus,
  setLogout,
  setUserState,
  resetLogoutFlag,
} = signinSlice.actions;

const signinSliceReducer = signinSlice.reducer;
export default signinSliceReducer;
