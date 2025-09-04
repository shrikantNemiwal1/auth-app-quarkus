import React from "react";
import { useAuth } from "../../context/AuthContext";
import Loading from "./Loading";

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <Loading />;
  return isAuthenticated ? <>{children}</> : null;
};

export default ProtectedRoute;
