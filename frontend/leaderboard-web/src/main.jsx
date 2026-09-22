import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
// Bootstrap 5 JS (dropdowns/modals data-API + Modal class), same behaviour the
// reference app got from the bootstrap bundle it loaded.
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './index.css';
import App from './App.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
