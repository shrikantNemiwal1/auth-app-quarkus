import { useEffect } from "react";
import { useAuth } from "../../context/AuthContext";

const EmailVerificationBanner = () => {
  const { user, refreshVerificationStatus } = useAuth();

  useEffect(() => {
    if (user && !user.emailVerified) {
      const interval = setInterval(refreshVerificationStatus, 30000);
      return () => clearInterval(interval);
    }
  }, [user, refreshVerificationStatus]);

  if (!user || user.emailVerified) {
    return (
      <div
        className="bg-green-800 border-l-4 border-green-400 text-green-100 p-4 rounded-lg mb-6 shadow-lg"
        role="alert"
      >
        <p className="font-bold">Email Verified</p>
        <p>Your email is validated. You have full access to the portal.</p>
      </div>
    );
  }

  return (
    <div
      className="bg-yellow-800 border-l-4 border-yellow-400 text-yellow-100 p-4 rounded-lg mb-6 shadow-lg"
      role="alert"
    >
      <p className="font-bold">Email Verification Required</p>
      <p>
        You need to validate your email to access all features. Please check your inbox for a
        verification link.
      </p>
    </div>
  );
};

export default EmailVerificationBanner;
