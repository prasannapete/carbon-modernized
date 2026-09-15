import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

// Bootstrap CSS (the original app used bootstrap 5.3.3)
import 'bootstrap/dist/css/bootstrap.css'
// App styles ported from the original web app (res/static/css/style.css)
import './styles/style.css'

import App from './App.jsx'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
)
