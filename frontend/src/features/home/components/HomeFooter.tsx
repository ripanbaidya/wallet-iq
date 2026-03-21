import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";
import { Link } from "react-router-dom";

export const CTASection: React.FC = () => (
  <section className="py-16 sm:py-24 px-4 sm:px-6 bg-white relative overflow-hidden">
    {/* Subtle light dot grid */}
    <div
      className="absolute inset-0 pointer-events-none"
      style={{
        backgroundImage:
          "radial-gradient(circle, #e5e7eb 1px, transparent 1px)",
        backgroundSize: "24px 24px",
      }}
    />

    {/* Accent glow behind headline */}
    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[520px] h-48 bg-[#e8ff4f]/20 rounded-full blur-3xl pointer-events-none" />

    <div className="relative max-w-3xl mx-auto text-center">
      {/* Eyebrow */}
      <p className="text-xs text-gray-400 uppercase tracking-widest mb-4">
        Get started today
      </p>

      {/* Headline */}
      <h2 className="text-3xl sm:text-4xl md:text-5xl font-black tracking-tight text-gray-900 leading-[1.05] mb-5">
        Ready to take
        <br />
        <span className="text-gray-300 italic">control?</span>
      </h2>

      {/* Subtext */}
      <p className="text-gray-400 text-sm mb-10 max-w-sm mx-auto leading-relaxed">
        Join WalletIQ free. No credit card. Full access to AI chat, budgets, and
        savings goals from day one.
      </p>

      {/* CTAs */}
      <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
        <Link
          to="/signup"
          className="w-full sm:w-auto inline-flex items-center justify-center gap-2 text-sm font-bold bg-gray-900 text-white px-8 py-3.5 rounded-xl hover:bg-black active:scale-[0.98] transition-all shadow-lg shadow-gray-900/10"
        >
          Create free account
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <line x1="5" y1="12" x2="19" y2="12" />
            <polyline points="12 5 19 12 12 19" />
          </svg>
        </Link>

        <Link
          to="/login"
          className="w-full sm:w-auto inline-flex items-center justify-center text-sm text-gray-400 border border-gray-200 px-8 py-3.5 rounded-xl hover:text-gray-900 hover:border-gray-400 hover:bg-gray-50 active:scale-[0.98] transition-all"
        >
          Already have an account? Log in
        </Link>
      </div>

      {/* Trust line */}
      <p className="mt-8 text-xs text-black">
        Free forever · No credit card required · Cancel anytime
      </p>
    </div>
  </section>
);

export const HomeFooter: React.FC = () => (
  <footer className="bg-black border-t border-gray-100">
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-4 sm:py-5">
      <div className="flex items-center">
        {/* Left spacer */}
        <div className="flex-1" />

        {/* Center (perfectly centered text) */}
        <p className="text-xs text-gray-400 text-center">
          © 2026 WalletIQ · Built by Ripan Baidya
        </p>

        {/* Right (icons) */}
        <div className="flex-1 flex justify-end items-center gap-2 sm:gap-3">
          <a
            href="https://github.com/ripanbaidya/wallet-iq"
            target="_blank"
            rel="noreferrer"
            aria-label="GitHub"
            className="w-7 h-7 sm:w-8 sm:h-8 flex items-center justify-center rounded-full text-gray-400 border border-gray-200 hover:text-gray-900 hover:border-gray-400 hover:bg-gray-50 transition-all"
          >
            <FaGithub size={14} />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            aria-label="Instagram"
            className="w-7 h-7 sm:w-8 sm:h-8 flex items-center justify-center rounded-full text-gray-400 border border-gray-200 hover:text-pink-500 hover:border-pink-200 hover:bg-pink-50 transition-all"
          >
            <FaInstagram size={14} />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            aria-label="LinkedIn"
            className="w-7 h-7 sm:w-8 sm:h-8 flex items-center justify-center rounded-full text-gray-400 border border-gray-200 hover:text-blue-500 hover:border-blue-200 hover:bg-blue-50 transition-all"
          >
            <FaLinkedin size={14} />
          </a>
        </div>
      </div>
    </div>
  </footer>
);
