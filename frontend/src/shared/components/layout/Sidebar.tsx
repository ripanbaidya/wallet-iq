import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { RiRepeat2Line } from "react-icons/ri";
import { ROUTES } from "../../../routes/routePaths";

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

import NotificationBell from "../../../features/notifications/components/NotificationBell";

// Nav items

type NavItem = {
  label: string;
  path: string;
  icon: React.ElementType;
};

type NavGroup = {
  heading?: string;
  items: NavItem[];
};

/**
 * Grouped nav structure — rendered in both desktop sidebar and mobile drawer.
 * Headings are shown in full-label mode; hidden in icon-only (tablet) mode.
 */
const NAV_GROUPS: NavGroup[] = [
  {
    heading: "Overview",
    items: [
      { label: "Dashboard", path: ROUTES.dashboard, icon: RiDashboardLine },
    ],
  },
  {
    heading: "Money",
    items: [
      {
        label: "Transactions",
        path: ROUTES.transactions,
        icon: RiExchangeDollarLine,
      },
      { label: "Recurring", path: ROUTES.recurring, icon: RiRepeat2Line },
      { label: "Budgets", path: ROUTES.budgets, icon: RiPieChartLine },
      { label: "Savings Goals", path: ROUTES.savings, icon: RiGovernmentLine },
    ],
  },
  {
    heading: "Settings",
    items: [
      { label: "Categories", path: ROUTES.categories, icon: RiPriceTag3Line },
      {
        label: "Payment Modes",
        path: ROUTES.paymentModes,
        icon: RiBankCardLine,
      },
    ],
  },
  {
    heading: "More",
    items: [
      { label: "Chat", path: ROUTES.chat, icon: RiChatSmile2Line },
      { label: "Profile", path: ROUTES.profile, icon: RiUserLine },
      { label: "About", path: ROUTES.about, icon: RiInformationLine },
    ],
  },
];

// Flat list — used for the icon-only tablet sidebar and the bottom tab bar
const NAV_ITEMS_FLAT: NavItem[] = NAV_GROUPS.flatMap((g) => g.items);

const ADMIN_NAV_ITEM: NavItem = {
  label: "Admin Panel",
  path: ROUTES.admin,
  icon: RiShieldUserLine,
};

/**
 * Bottom tab bar — 5 most important destinations, always visible on mobile.
 * Everything else is reachable via the drawer.
 */
const BOTTOM_TAB_PATHS = [
  "/dashboard",
  "/transactions",
  "/budgets",
  "/chat",
  "/profile",
];
const BOTTOM_TAB_ITEMS = NAV_ITEMS_FLAT.filter((item) =>
  BOTTOM_TAB_PATHS.includes(item.path),
);

// Helpers

const getInitials = (fullName?: string): string => {
  if (!fullName) return "U";
  return fullName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join("");
};

// Shared nav link renderers

/** Full nav link — icon + label — used in desktop sidebar and mobile drawer */
const FullNavLink: React.FC<{ item: NavItem; onClick?: () => void }> = ({
  item,
  onClick,
}) => {
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
const IconNavLink: React.FC<{ item: NavItem }> = ({ item }) => {
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

// Grouped nav renderer — desktop sidebar + mobile drawer

const GroupedNav: React.FC<{
  onItemClick?: () => void;
  showAdmin: boolean;
}> = ({ onItemClick, showAdmin }) => (
  <nav className="flex-1 px-3 py-4 overflow-y-auto space-y-4">
    {NAV_GROUPS.map((group) => (
      <div key={group.heading}>
        {group.heading && (
          <p className="px-3 mb-1 text-[10px] font-semibold uppercase tracking-widest text-gray-400 select-none">
            {group.heading}
          </p>
        )}
        <div className="space-y-0.5">
          {group.items.map((item) => (
            <FullNavLink key={item.path} item={item} onClick={onItemClick} />
          ))}
        </div>
      </div>
    ))}

    {showAdmin && (
      <div>
        <div className="mx-3 my-1 border-t border-gray-100" />
        <FullNavLink item={ADMIN_NAV_ITEM} onClick={onItemClick} />
      </div>
    )}
  </nav>
);

// User footer

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

// Main Sidebar

/**
 * Three responsive modes
 *
 * Mobile  (< md)  : fixed top bar + bottom tab bar (5 key items)
 *                   + hamburger → full drawer with all nav items
 * Tablet  (md)    : icon-only collapsed sidebar (w-16)
 * Desktop (lg+)   : full sidebar with grouped labels (w-60)
 */
const Sidebar: React.FC = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);

  const closeDrawer = () => setDrawerOpen(false);

  return (
    <>
      {/* DESKTOP sidebar (lg+) */}
      <aside className="hidden lg:flex w-60 min-h-screen bg-white border-r border-gray-100 flex-col">
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

        <GroupedNav showAdmin={isAdmin} />

        <UserFooter user={user} logout={logout} />
      </aside>

      {/* TABLET sidebar (md → lg) — icon only */}
      <aside className="hidden md:flex lg:hidden w-16 min-h-screen bg-white border-r border-gray-100 flex-col items-center">
        <div
          className="w-full flex items-center justify-center py-5 border-b border-gray-100 cursor-pointer select-none"
          onClick={() => navigate("/home")}
        >
          <span className="text-xl font-black text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            W
          </span>
        </div>

        <div className="py-2 flex justify-center border-b border-gray-100 w-full">
          <NotificationBell size="sm" align="left" />
        </div>

        <nav className="flex-1 flex flex-col items-center py-4 gap-1 overflow-y-auto w-full">
          {NAV_ITEMS_FLAT.map((item) => (
            <IconNavLink key={item.path} item={item} />
          ))}
          {isAdmin && (
            <>
              <div className="w-6 my-1 border-t border-gray-100" />
              <IconNavLink item={ADMIN_NAV_ITEM} />
            </>
          )}
        </nav>

        <UserFooter user={user} logout={logout} collapsed />
      </aside>

      {/* MOBILE top bar */}
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

      {/* Spacer — keeps content below the fixed top bar */}
      <div className="md:hidden h-14 shrink-0" />

      {/* MOBILE drawer */}
      {drawerOpen && (
        <>
          <div
            className="md:hidden fixed inset-0 bg-black/40 z-50"
            onClick={closeDrawer}
          />
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

            {/* Grouped nav — same as desktop */}
            <GroupedNav onItemClick={closeDrawer} showAdmin={isAdmin} />

            <UserFooter user={user} logout={logout} />
          </div>
        </>
      )}

      {/* MOBILE bottom tab bar */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-gray-100 flex items-center justify-around px-2 h-[calc(4rem_+_env(safe-area-inset-bottom))] pb-[env(safe-area-inset-bottom)]">
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

      {/* Spacer — keeps content above the fixed bottom tab bar */}
      <div className="md:hidden h-[calc(4rem_+_env(safe-area-inset-bottom))] shrink-0 order-last" />
    </>
  );
};

export default Sidebar;
