import { Link } from "react-router-dom";
import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";

/**
 * CTASection
 */
export const CTASection: React.FC = () => (
  <section className="py-24 px-6 bg-white">
    <div className="max-w-3xl mx-auto text-center">
      <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 leading-[1.05] mb-4">
        Ready to take
        <br />
        <span className="text-gray-300 italic">control?</span>
      </h2>

      <p className="text-gray-400 text-sm mb-10 max-w-sm mx-auto leading-relaxed">
        Join WalletIQ free. No credit card. Full access to AI chat, budgets, and
        savings goals from day one.
      </p>

      <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
        <Link
          to="/signup"
          className="w-full sm:w-auto text-sm font-bold bg-[#e8ff4f] text-gray-900 px-8 py-3.5 rounded-xl hover:bg-[#d4eb3a] transition-colors"
        >
          Create free account →
        </Link>

        <Link
          to="/login"
          className="w-full sm:w-auto text-sm text-gray-400 hover:text-gray-900 transition-colors"
        >
          Already have an account? Login
        </Link>
      </div>
    </div>
  </section>
);

/**
 * HomeFooter (Responsive Fix Applied)
 */
export const HomeFooter: React.FC = () => (
  <footer className="bg-[#0f0f0f] border-t border-white/5 py-6 px-6">
    <div className="max-w-6xl mx-auto">
      {/* Desktop Layout */}
      <div className="hidden sm:flex items-center justify-between">
        {/* Empty left space (for perfect centering balance) */}
        <div className="w-1/3" />

        {/* Center */}
        <p className="w-1/3 text-center text-white/35 text-xs">
          © 2026 WalletIQ · All rights reserved.
        </p>

        {/* Right Icons */}
        <div className="w-1/3 flex justify-end gap-5 text-white/40 text-xl">
          <a
            href="https://github.com/ripanbaidya/wallet-iq"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-white hover:scale-110"
          >
            <FaGithub />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-pink-500 hover:scale-110"
          >
            <FaInstagram />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-blue-500 hover:scale-110"
          >
            <FaLinkedin />
          </a>
        </div>
      </div>

      {/* Mobile Layout */}
      <div className="flex flex-col items-center gap-4 sm:hidden">
        {/* Icons */}
        <div className="flex gap-5 text-white/40 text-xl">
          <a
            href="https://github.com/ripanbaidya/wallet-iq"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-white"
          >
            <FaGithub />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-pink-500"
          >
            <FaInstagram />
          </a>

          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            className="transition-all hover:text-blue-500"
          >
            <FaLinkedin />
          </a>
        </div>

        {/* Copyright */}
        <p className="text-white/35 text-xs text-center">
          © 2026 WalletIQ · All rights reserved.
        </p>
      </div>
    </div>
  </footer>
);
