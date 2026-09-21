import Dashboard from "./components/screens/Dashboard";
import { Route, Routes } from "react-router-dom";
import AuthComponent from "./components/auth-component/auth-component";
import AuthCallback from "./components/auth-component/auth-callback";
import SigninPage from "./screens/user-signup-signin/signin/index";
import Settings from "./components/screens/Settings";
import UserForm from "./components/screens/UserForm";
import 'bootstrap/dist/css/bootstrap.min.css';
import ResetPassword from "./components/screens/ResetPassword";

function App() {
  return (
      <Routes>
          <Route path="/signin/*" element={<SigninPage />} />
    <Route
        path="/*"
        element={
            <>
                <AuthComponent>
                    <Dashboard />
                </AuthComponent>
            </>
        }
    />
          <Route path="/oauth2/callback" element={<AuthCallback />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/users" element={<UserForm />} />
          <Route path="/reset-password" element={<ResetPassword />} />
      </Routes>
  );
}

export default App;
