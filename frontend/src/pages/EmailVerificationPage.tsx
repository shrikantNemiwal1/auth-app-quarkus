import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import AuthLayout from "../components/auth/AuthLayout";

const EmailVerificationPage = ({
  navigate,
}: {
  navigate: (path: string) => void;
}) => {
  const [status, setStatus] = useState<"verifying" | "success" | "error">(
    "verifying"
  );
  const [message, setMessage] = useState(
    "Verifying your email, please wait..."
  );
  const { verifyEmail, checkSession } = useAuth();

  useEffect(() => {
    const verify = async () => {
      const params = new URLSearchParams(window.location.search);
      const token = params.get("token");

      if (!token) {
        setStatus("error");
        setMessage("No verification token found. The link may be invalid.");
        return;
      }

      try {
        await verifyEmail(token);
        await checkSession();

        setStatus("success");
        setMessage("Email successfully verified! Redirecting...");

        window.history.replaceState({}, "", "/verify");
        setTimeout(() => navigate("/dashboard"), 1500);
      } catch (err: any) {
        setStatus("error");
        setMessage(
          err.message ||
            "Failed to verify email. The token might be expired or invalid."
        );
      }
    };

    verify();
  }, [verifyEmail, checkSession, navigate]);

  const statusColors = {
    verifying: "text-blue-400",
    success: "text-green-400",
    error: "text-red-400",
  };

  return (
    <AuthLayout title="Email Verification">
      <div className="text-center">
        {status === "verifying" && (
          <div className="w-12 h-12 border-4 border-dashed rounded-full animate-spin border-blue-500 mx-auto mb-4"></div>
        )}
        <p className={`text-lg ${statusColors[status]}`}>{message}</p>
      </div>
    </AuthLayout>
  );
};

export default EmailVerificationPage;
