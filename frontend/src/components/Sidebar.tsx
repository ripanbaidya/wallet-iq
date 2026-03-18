import { NavLink } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const navItems = [
    { label: "Dashboard",     path: "/dashboard" },
    { label: "Transactions",  path: "/transactions" },
    { label: "Categories",    path: "/categories" },
    { label: "Budgets",       path: "/budgets" },
    { label: "Savings Goals", path: "/savings" },
    { label: "Chat",          path: "/chat" },
    { label: "Profile",       path: "/profile" },
];

const Sidebar: React.FC = () => {
    const { user, logout } = useAuth();

    return (
        <aside className="w-56 min-h-screen bg-white border-r border-gray-200 flex flex-col">
            {/* Logo */}
            <div className="px-5 py-5 border-b border-gray-200">
                <span className="text-lg font-semibold tracking-tight text-black">WalletIQ</span>
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
            <div className="px-4 py-4 border-t border-gray-200">
                <p className="text-xs text-gray-400 truncate mb-2">{user?.email}</p>
                <button
                    onClick={logout}
                    className="w-full text-left text-sm text-red-500 hover:text-red-700 transition-colors"
                >
                    Logout
                </button>
            </div>
        </aside>
    );
};

export default Sidebar;