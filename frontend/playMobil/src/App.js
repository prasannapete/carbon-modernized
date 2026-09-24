import { Route, Routes } from "react-router-dom";
import AuthComponent from "./components/auth-component/auth-component";
import AuthCallback from "./components/auth-component/auth-callback";
import SigninPage from "./screens/user-signup-signin/signin/index";
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
                    <UserForm />
                </AuthComponent>
            </>
        }
    />
          <Route path="/oauth2/callback" element={<AuthCallback />} />
          <Route path="/reset-password" element={<ResetPassword />} />
      </Routes>
  );
}

export default App;
