import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { RiRepeat2Line } from "react-icons/ri";

import {
  RiDashboardLine,
  RiExchangeDollarLine,
  RiPriceTag3Line,
  RiBankCardLine,
  RiPieChartLine,
  RiGovernmentLine,
  RiChatSmile2Line,
  RiUserLine,
  RiInformationLine,
  RiLogoutBoxRLine,
  RiMenuLine,
  RiCloseLine,
  RiShieldUserLine,
} from "react-icons/ri";

// ── [CHANGE 1] Import NotificationBell ────────────────────────────────────────
import NotificationBell from "../components/notifications/NotificationBell";

const NAV_ITEMS = [
  { label: "Dashboard", path: "/dashboard", icon: RiDashboardLine },
  { label: "Transactions", path: "/transactions", icon: RiExchangeDollarLine },
  { label: "Recurring", path: "/recurring", icon: RiRepeat2Line },
  { label: "Categories", path: "/categories", icon: RiPriceTag3Line },
  { label: "Payment Modes", path: "/payment-modes", icon: RiBankCardLine },
  { label: "Budgets", path: "/budgets", icon: RiPieChartLine },
  { label: "Savings Goals", path: "/savings", icon: RiGovernmentLine },
  { label: "Chat", path: "/chat", icon: RiChatSmile2Line },
  { label: "Profile", path: "/profile", icon: RiUserLine },
  { label: "About", path: "/about", icon: RiInformationLine },
];

const ADMIN_NAV_ITEM = {
  label: "Admin Panel",
  path: "/admin",
  icon: RiShieldUserLine,
};

/**
 * Bottom tab bar shows only the 5 most important items on mobile.
 * The rest are accessible via the drawer.
 */
const BOTTOM_TAB_PATHS = [
  "/dashboard",
  "/transactions",
  "/budgets",
  "/chat",
  "/profile",
];

const BOTTOM_TAB_ITEMS = NAV_ITEMS.filter((item) =>
  BOTTOM_TAB_PATHS.includes(item.path),
);

// Helper
const getInitials = (fullName?: string): string => {
  if (!fullName) return "U";
  return fullName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join("");
};

// ─── Shared nav link renderer ─────────────────────────────────────────────────

/** Full nav link — icon + label, used in desktop sidebar and mobile drawer */
const FullNavLink: React.FC<{
  item: (typeof NAV_ITEMS)[number];
  onClick?: () => void;
}> = ({ item, onClick }) => {
  const Icon = item.icon;
  return (
    <NavLink
      to={item.path}
      onClick={onClick}
      className={({ isActive }) =>
        `flex items-center gap-3 px-3 py-2.5 rounded-lg text-[13.5px] transition-all duration-150 ${
          isActive
            ? "bg-gray-900 text-white font-medium"
            : "text-gray-500 hover:text-gray-900 hover:bg-gray-50"
        }`
      }
    >
      {({ isActive }) => (
        <>
          <span className={isActive ? "text-[#e8ff4f]" : "text-gray-400"}>
            <Icon size={17} />
          </span>
          {item.label}
        </>
      )}
    </NavLink>
  );
};

/** Icon-only nav link — used in collapsed tablet sidebar */
const IconNavLink: React.FC<{ item: (typeof NAV_ITEMS)[number] }> = ({
  item,
}) => {
  const Icon = item.icon;
  return (
    <NavLink
      to={item.path}
      title={item.label}
      className={({ isActive }) =>
        `flex items-center justify-center w-10 h-10 rounded-lg transition-all duration-150 ${
          isActive
            ? "bg-gray-900 text-[#e8ff4f]"
            : "text-gray-400 hover:text-gray-900 hover:bg-gray-50"
        }`
      }
    >
      <Icon size={19} />
    </NavLink>
  );
};

// ─── User footer — shared between desktop sidebar and mobile drawer ────────────
const UserFooter: React.FC<{
  user: ReturnType<typeof useAuth>["user"];
  logout: () => void;
  collapsed?: boolean;
}> = ({ user, logout, collapsed = false }) => {
  const initials = getInitials(user?.fullName);

  if (collapsed) {
    return (
      <div className="border-t border-gray-100 p-2 flex flex-col items-center gap-2">
        <div className="w-8 h-8 rounded-full bg-[#e8ff4f] flex items-center justify-center">
          <span className="text-gray-900 text-xs font-black select-none">
            {initials}
          </span>
        </div>
        <button
          onClick={logout}
          title="Sign out"
          className="text-gray-300 hover:text-red-500 transition-colors p-1"
        >
          <RiLogoutBoxRLine size={16} />
        </button>
      </div>
    );
  }

  return (
    <div className="border-t border-gray-100 p-3">
      <div className="flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-gray-50 transition-colors">
        <div className="w-8 h-8 rounded-full bg-[#e8ff4f] flex items-center justify-center shrink-0">
          <span className="text-gray-900 text-xs font-black select-none">
            {initials}
          </span>
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-medium text-gray-900 truncate leading-tight">
            {user?.fullName ?? "User"}
          </p>
          <button
            onClick={logout}
            className="text-xs text-gray-400 hover:text-red-500 transition-colors mt-0.5"
          >
            Sign out
          </button>
        </div>
        <button
          onClick={logout}
          title="Sign out"
          className="shrink-0 text-gray-300 hover:text-red-500 transition-colors p-1 rounded"
        >
          <RiLogoutBoxRLine size={15} />
        </button>
      </div>
    </div>
  );
};

// ─── Admin divider + link block ───────────────────────────────────────────────

const AdminNavSection: React.FC<{ onClick?: () => void }> = ({ onClick }) => (
  <>
    <div className="mx-3 my-2 border-t border-gray-100" />
    <FullNavLink item={ADMIN_NAV_ITEM} onClick={onClick} />
  </>
);

const AdminIconNavSection: React.FC = () => (
  <>
    <div className="w-6 my-1 border-t border-gray-100" />
    <IconNavLink item={ADMIN_NAV_ITEM} />
  </>
);

// ─── Main Sidebar component ───────────────────────────────────────────────────

/**
 * Sidebar — three responsive modes:
 *
 * Mobile  (< md)  : hidden sidebar + bottom tab bar (5 key items)
 *                   + hamburger button → full-screen drawer overlay
 * Tablet  (md)    : icon-only collapsed sidebar (w-16)
 * Desktop (lg+)   : full sidebar with labels (w-60)
 */
const Sidebar: React.FC = () => {
  // isAdmin is set in the auth store at login time by probing /admin/users/count
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);

  const closeDrawer = () => setDrawerOpen(false);

  return (
    <>
      {/* DESKTOP sidebar  (lg+) — full labels */}
      <aside className="hidden lg:flex w-60 min-h-screen bg-white border-r border-gray-100 flex-col">
        {/* ── [CHANGE 2] Desktop logo header — bell sits right of logo ── */}
        <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
          <span
            className="text-xl font-bold tracking-tight text-gray-900 cursor-pointer select-none"
            onClick={() => navigate("/home")}
          >
            Wallet
            <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
              IQ
            </span>
          </span>
          <NotificationBell align="left" />
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
          {NAV_ITEMS.map((item) => (
            <FullNavLink key={item.path} item={item} />
          ))}
          {isAdmin && <AdminNavSection />}
        </nav>

        <UserFooter user={user} logout={logout} />
      </aside>

      {/* TABLET sidebar  (md → lg) — icon only, no labels */}
      <aside className="hidden md:flex lg:hidden w-16 min-h-screen bg-white border-r border-gray-100 flex-col items-center">
        {/* Logo — just "W" */}
        <div
          className="w-full flex items-center justify-center py-5 border-b border-gray-100 cursor-pointer select-none"
          onClick={() => navigate("/home")}
        >
          <span className="text-xl font-black text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            W
          </span>
        </div>

        {/* ── [CHANGE 3] Tablet — bell icon between logo and nav ── */}
        <div className="py-2 flex justify-center border-b border-gray-100 w-full">
          <NotificationBell size="sm" align="left" />
        </div>

        {/* Icon-only nav */}
        <nav className="flex-1 flex flex-col items-center py-4 gap-1 overflow-y-auto">
          {NAV_ITEMS.map((item) => (
            <IconNavLink key={item.path} item={item} />
          ))}
          {isAdmin && <AdminIconNavSection />}
        </nav>

        <UserFooter user={user} logout={logout} collapsed />
      </aside>

      {/* ════════════════════════════════════════════════════════════════════
          MOBILE  (< md)
          ════════════════════════════════════════════════════════════════════ */}

      {/* ── [CHANGE 4] Mobile top bar — bell sits between logo and hamburger ── */}
      <div className="md:hidden fixed top-0 left-0 right-0 z-40 bg-white border-b border-gray-100 flex items-center justify-between px-4 h-14">
        <span
          className="text-lg font-bold tracking-tight text-gray-900 cursor-pointer select-none"
          onClick={() => navigate("/home")}
        >
          Wallet
          <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            IQ
          </span>
        </span>
        <div className="flex items-center gap-1">
          <NotificationBell align="right" />
          <button
            onClick={() => setDrawerOpen(true)}
            className="p-2 rounded-lg text-gray-500 hover:bg-gray-50 transition-colors"
            aria-label="Open menu"
          >
            <RiMenuLine size={22} />
          </button>
        </div>
      </div>

      {/* Spacer so content doesn't hide behind the top bar */}
      <div className="md:hidden h-14 shrink-0" />

      {/* ── Mobile drawer overlay ── */}
      {drawerOpen && (
        <>
          {/* Backdrop */}
          <div
            className="md:hidden fixed inset-0 bg-black/40 z-50"
            onClick={closeDrawer}
          />

          {/* Drawer panel — slides in from left */}
          <div className="md:hidden fixed top-0 left-0 bottom-0 w-72 bg-white z-50 flex flex-col shadow-2xl">
            {/* Drawer header */}
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <span
                className="text-xl font-bold tracking-tight text-gray-900 cursor-pointer"
                onClick={() => {
                  navigate("/home");
                  closeDrawer();
                }}
              >
                Wallet
                <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
                  IQ
                </span>
              </span>
              <button
                onClick={closeDrawer}
                className="p-2 rounded-lg text-gray-400 hover:bg-gray-50 transition-colors"
                aria-label="Close menu"
              >
                <RiCloseLine size={20} />
              </button>
            </div>

            {/* All nav links */}
            <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
              {NAV_ITEMS.map((item) => (
                <FullNavLink
                  key={item.path}
                  item={item}
                  onClick={closeDrawer}
                />
              ))}
              {isAdmin && <AdminNavSection onClick={closeDrawer} />}
            </nav>

            <UserFooter user={user} logout={logout} />
          </div>
        </>
      )}

      {/* ── Mobile bottom tab bar ── */}
      {/* Shows 5 key destinations — always visible, drawer for the rest */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-gray-100 flex items-center justify-around px-2 h-16 safe-area-inset-bottom">
        {BOTTOM_TAB_ITEMS.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex flex-col items-center gap-0.5 px-3 py-1.5 rounded-xl transition-colors ${
                  isActive ? "text-gray-900" : "text-gray-400"
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <span
                    className={`p-1.5 rounded-xl transition-colors ${
                      isActive ? "bg-gray-900" : ""
                    }`}
                  >
                    <Icon
                      size={19}
                      className={isActive ? "text-[#e8ff4f]" : ""}
                    />
                  </span>
                  <span className="text-[10px] font-medium leading-none">
                    {item.label}
                  </span>
                </>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Spacer so page content clears the bottom tab bar on mobile */}
      <div className="md:hidden h-16 shrink-0 order-last" />
    </>
  );
};

export default Sidebar;
