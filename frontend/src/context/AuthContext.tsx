// src/context/AuthContext.tsx
import { createContext, useContext, useState, useEffect } from "react";
import api from "../services/api";

interface User {
  userId: string;
  email: string;
  emailVerified: boolean;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  checkSession: () => Promise<void>;
  verifyEmail: (token: string) => Promise<void>;
  refreshVerificationStatus: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const login = async (email: string, password: string) => {
    const loggedInUser = await api.login(email, password);
    setUser(loggedInUser);
  };

  const logout = async () => {
    await api.logout();
    setUser(null);
  };

  const signup = async (email: string, password: string) => {
    await api.signup(email, password);
  };

  const checkSession = async () => {
    try {
      const sessionUser = await api.getSession();
      setUser(sessionUser);
    } catch (err: any) {
      // If it's 401, treat as "not logged in" instead of looping
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  const verifyEmail = async (token: string) => {
    await api.verifyEmail(token);
    await checkSession();
  };

  const refreshVerificationStatus = async () => {
    await checkSession();
  };

  // Run on mount + every 60s
  useEffect(() => {
    checkSession();
    const interval = setInterval(checkSession, 60000);
    return () => clearInterval(interval);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        signup,
        checkSession,
        verifyEmail,
        refreshVerificationStatus,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
