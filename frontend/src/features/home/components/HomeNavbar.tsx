import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../../shared/hooks/useAuth";
import { getAvatarUrl } from "../../../shared/utils/profileHelpers";

const NAV_LINKS = [
  { label: "Features", href: "#features" },
  { label: "How it works", href: "#how-it-works" },
  { label: "Testimonials", href: "#testimonials" },
];

/**
 * HomeNavbar
 * - Transparent over the dark hero, transitions to white/blurred on scroll
 * - Accent colour (#e8ff4f) on the "IQ" logo and CTA button
 */
const HomeNavbar: React.FC = () => {
  const [scrolled, setScrolled] = useState(false);
  const { isAuthenticated, user } = useAuth();
  const avatarUrl = getAvatarUrl({
    id: user?.id,
    email: user?.email,
    fullName: user?.fullName,
  });

  useEffect(() => {
    const handler = () => setScrolled(window.scrollY > 40);
    window.addEventListener("scroll", handler, { passive: true });
    return () => window.removeEventListener("scroll", handler);
  }, []);

  return (
    <nav
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled
          ? "bg-white/95 backdrop-blur-md border-b border-gray-200 shadow-sm"
          : "bg-transparent"
      }`}
    >
      <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to="/home" className="text-2xl font-bold tracking-tight">
          <span className={scrolled ? "text-gray-900" : "text-white"}>
            Wallet
          </span>
          <span className="text-[#e8ff4f]">IQ</span>
        </Link>

        {/* Center nav links */}
        <div className="hidden md:flex items-center gap-8">
          {NAV_LINKS.map((link) => (
            <a
              key={link.label}
              href={link.href}
              className={`text-sm transition-colors duration-200 ${
                scrolled
                  ? "text-gray-500 hover:text-gray-900"
                  : "text-white/70 hover:text-white"
              }`}
            >
              {link.label}
            </a>
          ))}
        </div>

        {/* Auth */}
        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <Link
                to="/dashboard"
                className={`text-sm transition-colors px-3 py-2 ${
                  scrolled
                    ? "text-gray-600 hover:text-gray-900"
                    : "text-white/80 hover:text-white"
                }`}
              >
                Dashboard
              </Link>
              <Link
                to="/profile"
                className="rounded-full border border-white/20 hover:border-[#e8ff4f] transition-colors"
                aria-label="Go to profile"
                title="Profile"
              >
                <img
                  src={avatarUrl}
                  alt="Profile"
                  className="w-9 h-9 rounded-full bg-white object-cover"
                  loading="lazy"
                />
              </Link>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className={`text-sm transition-colors px-3 py-2 ${
                  scrolled
                    ? "text-gray-600 hover:text-gray-900"
                    : "text-white/80 hover:text-white"
                }`}
              >
                Login
              </Link>
              <Link
                to="/signup"
                className="text-sm font-semibold bg-[#e8ff4f] text-gray-900 px-4 py-2 rounded-lg hover:bg-[#d4eb3a] transition-colors"
              >
                Get started
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default HomeNavbar;
