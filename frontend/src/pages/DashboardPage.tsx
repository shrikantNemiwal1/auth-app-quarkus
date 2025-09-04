import { useAuth } from "../context/AuthContext";
import DashboardLayout from "../components/dashboard/DashboardLayout";
import EmailVerificationBanner from "../components/dashboard/EmailVerificationBanner";

interface PageProps {
  navigate: (path: string) => void;
}

const DashboardPage = ({ navigate }: PageProps) => {
  const { user } = useAuth();

  return (
    <DashboardLayout navigate={navigate}>
      <h1 className="text-4xl font-bold mb-6 text-blue-300">Dashboard</h1>
      <EmailVerificationBanner />
      <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
        <h2 className="text-2xl font-semibold mb-4">Portal Content</h2>
        <p className="text-gray-400">
          {user?.emailVerified
            ? "Welcome! All portal features are available to you."
            : "Some features may be limited until your email is verified. Please check the banner above."}
        </p>
      </div>
    </DashboardLayout>
  );
};

export default DashboardPage;
