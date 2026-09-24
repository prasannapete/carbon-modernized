import {configureStore} from "@reduxjs/toolkit";
import signinSliceReducer from "../slices/signin-slice";
import countryFilterReducer from "../slices/countryFilter-slice"
import {apiSlice} from "./api";

const store = configureStore({
  reducer: {
    signinSliceReducer: signinSliceReducer,
    countryFilter: countryFilterReducer,
    [apiSlice.reducerPath]: apiSlice.reducer
  },
  middleware: getDefaultMiddleware => getDefaultMiddleware().concat(apiSlice.middleware)
});

export default store;
