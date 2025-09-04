import { useAuth } from "../../context/AuthContext";

interface PageProps {
  navigate: (path: string) => void;
}

const Header = ({ navigate }: PageProps) => {
  const { user, logout } = useAuth();
  if (!user) return null;

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <header className="bg-gray-800 p-4 shadow-md w-full">
      <div className="container mx-auto flex justify-between items-center">
        <span className="text-gray-300">
          Welcome, <span className="font-semibold text-white">{user.email}</span>
        </span>
        <button
          onClick={handleLogout}
          className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300"
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Header;
