import { createSlice } from "@reduxjs/toolkit";
import Cookies from "js-cookie";

const savedCountries = JSON.parse(localStorage.getItem("selectedCountries")) || [];

const countryFilterSlice = createSlice({
  name: "countryFilter",
  initialState: {
    selectedCountries: savedCountries || localStorage.getItem("selectedCountries")
  },
  reducers: {
    setSelectedCountries: (state, action) => {
      state.selectedCountries = action.payload;
      localStorage.setItem("selectedCountries", JSON.stringify(action.payload));
    }
  }
});

export const { setSelectedCountries } = countryFilterSlice.actions;
export default countryFilterSlice.reducer;