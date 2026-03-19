import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

/**
 * Layout
 *
 * Responsive shell:
 *   Desktop (lg+) : sidebar left, main fills the rest
 *   Tablet  (md)  : icon-only sidebar left, main fills the rest
 *   Mobile  (< md): no sidebar — Sidebar renders a fixed top bar +
 *                   fixed bottom tab bar instead. Main gets top/bottom
 *                   padding so content is never hidden behind them.
 */
const Layout: React.FC = () => (
  <div className="flex min-h-screen bg-gray-50">
    <Sidebar />

    {/*
      Main content area.
      - On mobile: pt-14 clears the fixed top bar, pb-20 clears the bottom tab bar.
      - On md+: no extra padding needed (sidebar is inline, not fixed).
    */}
    <main className="flex-1 overflow-auto p-4 md:p-6 pt-4 md:pt-6 pb-20 md:pb-6">
      <Outlet />
    </main>
  </div>
);

export default Layout;
