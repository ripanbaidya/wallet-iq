import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const navItems = [
  { label: "Dashboard", path: "/dashboard" },
  { label: "Transactions", path: "/transactions" },
  { label: "Categories", path: "/categories" },
  { label: "Payment Modes", path: "/payment-modes" },
  { label: "Budgets", path: "/budgets" },
  { label: "Savings Goals", path: "/savings" },
  { label: "Chat", path: "/chat" },
  { label: "Profile", path: "/profile" },
];

const extractFirstLetter = (fullName?: string) => {
  return fullName?.charAt(0).toUpperCase() ?? "U";
};

const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <aside className="w-56 min-h-screen bg-white border-r border-gray-200 flex flex-col">
      {/* Logo */}
      <div className="px-5 py-5 border-b border-gray-200">
        <span
          className="text-lg font-semibold tracking-tight text-black cursor-pointer"
          onClick={() => navigate("/home")}
        >
          WalletIQ
        </span>
      </div>

      {/* Nav Links */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `block px-3 py-2 rounded text-sm transition-colors ${
                isActive
                  ? "bg-gray-100 text-black font-medium"
                  : "text-gray-500 hover:text-black hover:bg-gray-50"
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* User + Logout */}
      <div className="px-4 py-4 border-t border-gray-200 flex items-center gap-3">
        {/* Avatar */}
        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-semibold">
          {extractFirstLetter(user?.fullName ?? "")}
        </div>

        {/* User Info + Logout */}
        <div className="flex-1 min-w-0">
          <p className="text-sm text-gray-700 truncate">{user?.fullName}</p>
          <button
            onClick={logout}
            className="text-xs text-red-500 hover:text-red-700 transition-colors"
          >
            Logout
          </button>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
