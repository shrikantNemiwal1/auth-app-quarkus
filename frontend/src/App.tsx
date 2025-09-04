import { useEffect, useState } from "react";
import { useAuth } from "./context/AuthContext";

import Loading from "./components/common/Loading";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import DashboardPage from "./pages/DashboardPage";
import EmailVerificationPage from "./pages/EmailVerificationPage";

const App = () => {
  const [route, setRoute] = useState(() => {
    if (window.location.pathname === "/verify" && window.location.search.includes("token=")) {
      return "/verify";
    }
    return "/login";
  });
  const { isAuthenticated, isLoading } = useAuth();

  const navigate = (path: string) => {
    setRoute(path);
  };

  useEffect(() => {
    if (!isLoading) {
      const isAuthPage = route === "/login" || route === "/signup";
      if (isAuthenticated && isAuthPage) {
        navigate("/dashboard");
      }
      if (!isAuthenticated && route === "/dashboard") {
        navigate("/login");
      }
    }
  }, [route, isAuthenticated, isLoading]);

  if (isLoading) return <Loading />;

  const renderPage = () => {
    if (route.startsWith("/dashboard")) {
      return isAuthenticated ? <DashboardPage navigate={navigate} /> : <LoginPage navigate={navigate} />;
    }
    switch (route) {
      case "/signup":
        return <SignupPage navigate={navigate} />;
      case "/verify":
        return <EmailVerificationPage navigate={navigate} />;
      case "/login":
      default:
        return <LoginPage navigate={navigate} />;
    }
  };

  return renderPage();
};

export default function ProvidedApp() {
  return <App />;
}
