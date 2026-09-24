import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { Provider as ReduxProvider } from "react-redux";
import store from "./redux/store/store";
import { BrowserRouter as Router } from "react-router-dom";
import { GlobalContextProvider } from "./context";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
<GlobalContextProvider>
    <ReduxProvider store={store}>
        <Router>
            <App />
        </Router>
    </ReduxProvider>
</GlobalContextProvider>
);
