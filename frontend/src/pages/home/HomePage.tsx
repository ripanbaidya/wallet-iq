import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

function useCounter(target: number, duration = 1800, start = false) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (!start) return;
    let startTime: number | null = null;
    const step = (timestamp: number) => {
      if (!startTime) startTime = timestamp;
      const progress = Math.min((timestamp - startTime) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setCount(Math.floor(eased * target));
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }, [start, target, duration]);
  return count;
}

// ── Intersection observer hook ────────────────────────────────────────────────
function useInView(threshold = 0.15) {
  const ref = useRef<HTMLDivElement>(null);
  const [inView, setInView] = useState(false);
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const obs = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setInView(true);
          obs.disconnect();
        }
      },
      { threshold },
    );
    obs.observe(el);
    return () => obs.disconnect();
  }, [threshold]);
  return { ref, inView };
}

// ── Stat counter card ─────────────────────────────────────────────────────────
function StatCard({
  value,
  suffix,
  label,
  inView,
}: {
  value: number;
  suffix: string;
  label: string;
  inView: boolean;
}) {
  const count = useCounter(value, 2000, inView);
  return (
    <div className="text-center">
      <div className="text-5xl font-black tracking-tight text-white">
        {count.toLocaleString()}
        <span className="text-emerald-400">{suffix}</span>
      </div>
      <div className="text-sm text-slate-400 mt-2 font-medium uppercase tracking-widest">
        {label}
      </div>
    </div>
  );
}

// ── Feature card ──────────────────────────────────────────────────────────────
function FeatureCard({
  icon,
  title,
  desc,
  delay,
}: {
  icon: string;
  title: string;
  desc: string;
  delay: string;
}) {
  const { ref, inView } = useInView();
  return (
    <div
      ref={ref}
      className="group relative bg-white/[0.03] border border-white/10 rounded-2xl p-7 hover:bg-white/[0.06] hover:border-emerald-500/30 transition-all duration-500"
      style={{
        opacity: inView ? 1 : 0,
        transform: inView ? "translateY(0)" : "translateY(32px)",
        transition: `opacity 0.6s ease ${delay}, transform 0.6s ease ${delay}`,
      }}
    >
      <div className="text-3xl mb-4">{icon}</div>
      <h3 className="text-white font-bold text-lg mb-2">{title}</h3>
      <p className="text-slate-400 text-sm leading-relaxed">{desc}</p>
      <div className="absolute inset-0 rounded-2xl bg-gradient-to-br from-emerald-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none" />
    </div>
  );
}

// ── Step card ─────────────────────────────────────────────────────────────────
function StepCard({
  num,
  title,
  desc,
  delay,
}: {
  num: string;
  title: string;
  desc: string;
  delay: string;
}) {
  const { ref, inView } = useInView();
  return (
    <div
      ref={ref}
      className="flex gap-5"
      style={{
        opacity: inView ? 1 : 0,
        transform: inView ? "translateX(0)" : "translateX(-24px)",
        transition: `opacity 0.6s ease ${delay}, transform 0.6s ease ${delay}`,
      }}
    >
      <div className="shrink-0 w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400 font-black text-lg">
        {num}
      </div>
      <div>
        <h3 className="text-white font-bold mb-1">{title}</h3>
        <p className="text-slate-400 text-sm leading-relaxed">{desc}</p>
      </div>
    </div>
  );
}

// ── Testimonial ───────────────────────────────────────────────────────────────
function Testimonial({
  quote,
  name,
  role,
  delay,
}: {
  quote: string;
  name: string;
  role: string;
  delay: string;
}) {
  const { ref, inView } = useInView();
  return (
    <div
      ref={ref}
      className="bg-white/[0.03] border border-white/10 rounded-2xl p-6"
      style={{
        opacity: inView ? 1 : 0,
        transform: inView ? "translateY(0)" : "translateY(24px)",
        transition: `opacity 0.5s ease ${delay}, transform 0.5s ease ${delay}`,
      }}
    >
      <div className="text-emerald-400 text-xl mb-3">"</div>
      <p className="text-slate-300 text-sm leading-relaxed mb-4">{quote}</p>
      <div className="flex items-center gap-3">
        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-emerald-400 to-cyan-500 flex items-center justify-center text-black text-xs font-bold">
          {name[0]}
        </div>
        <div>
          <div className="text-white text-sm font-semibold">{name}</div>
          <div className="text-slate-500 text-xs">{role}</div>
        </div>
      </div>
    </div>
  );
}

// ── Main Page ─────────────────────────────────────────────────────────────────
export default function HomePage() {
  const [scrolled, setScrolled] = useState(false);
  const [heroVisible, setHeroVisible] = useState(false);
  const statsRef = useRef<HTMLDivElement>(null);
  const [statsInView, setStatsInView] = useState(false);

  useEffect(() => {
    // Hero entrance
    const t = setTimeout(() => setHeroVisible(true), 100);

    // Navbar scroll
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener("scroll", onScroll);

    // Stats observer
    const statsEl = statsRef.current;
    if (statsEl) {
      const obs = new IntersectionObserver(
        ([e]) => {
          if (e.isIntersecting) {
            setStatsInView(true);
            obs.disconnect();
          }
        },
        { threshold: 0.2 },
      );
      obs.observe(statsEl);
      return () => {
        clearTimeout(t);
        window.removeEventListener("scroll", onScroll);
        obs.disconnect();
      };
    }

    return () => {
      clearTimeout(t);
      window.removeEventListener("scroll", onScroll);
    };
  }, []);

  return (
    <div className="min-h-screen bg-[#070b12] text-white font-sans overflow-x-hidden">
      {/* ── Google Fonts ── */}
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;700;800;900&family=DM+Sans:ital,wght@0,300;0,400;0,500;0,600;1,300&display=swap');

        * { font-family: 'DM Sans', sans-serif; }
        .font-display { font-family: 'Syne', sans-serif; }

        .hero-glow {
          background: radial-gradient(ellipse 80% 60% at 50% 0%, rgba(16,185,129,0.12) 0%, transparent 70%);
        }
        .grid-bg {
          background-image: 
            linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
            linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
          background-size: 48px 48px;
        }
        .text-gradient {
          background: linear-gradient(135deg, #10b981 0%, #06b6d4 50%, #6366f1 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        .cta-glow:hover {
          box-shadow: 0 0 32px rgba(16,185,129,0.4), 0 0 64px rgba(16,185,129,0.15);
        }
        .nav-blur {
          backdrop-filter: blur(16px);
          -webkit-backdrop-filter: blur(16px);
        }
        @keyframes float {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-10px); }
        }
        @keyframes pulse-ring {
          0% { transform: scale(1); opacity: 0.6; }
          100% { transform: scale(1.6); opacity: 0; }
        }
        .float { animation: float 4s ease-in-out infinite; }
        .float-delay { animation: float 4s ease-in-out infinite 1.5s; }
        .pulse-dot::before {
          content: '';
          position: absolute;
          inset: -4px;
          border-radius: 50%;
          border: 1px solid #10b981;
          animation: pulse-ring 2s ease-out infinite;
        }
      `}</style>

      {/* ── Navbar ── */}
      <nav
        className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
          scrolled ? "nav-blur bg-[#070b12]/80 border-b border-white/10" : ""
        }`}
      >
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          {/* Logo */}
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-emerald-400 to-cyan-500 flex items-center justify-center">
              <span className="text-black font-black text-xs">W</span>
            </div>
            <span className="font-display font-800 text-white text-lg tracking-tight">
              Wallet<span className="text-emerald-400">IQ</span>
            </span>
          </div>

          {/* Nav links */}
          <div className="hidden md:flex items-center gap-8">
            {["Features", "How it Works", "Testimonials"].map((item) => (
              <a
                key={item}
                href={`#${item.toLowerCase().replace(/ /g, "-")}`}
                className="text-sm text-slate-400 hover:text-white transition-colors duration-200"
              >
                {item}
              </a>
            ))}
          </div>

          {/* Auth buttons */}
          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="text-sm text-slate-300 hover:text-white transition-colors duration-200 px-4 py-2"
            >
              Login
            </Link>
            <Link
              to="/signup"
              className="text-sm font-semibold bg-emerald-500 hover:bg-emerald-400 text-black px-5 py-2 rounded-xl transition-all duration-200 cta-glow"
            >
              Get started free
            </Link>
          </div>
        </div>
      </nav>

      {/* ── Hero ── */}
      <section className="relative min-h-screen flex flex-col items-center justify-center pt-16 hero-glow grid-bg">
        {/* Background orbs */}
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-emerald-500/5 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-1/3 right-1/4 w-64 h-64 bg-cyan-500/5 rounded-full blur-3xl pointer-events-none" />

        <div className="relative max-w-5xl mx-auto px-6 text-center">
          {/* Badge */}
          <div
            className="inline-flex items-center gap-2 bg-emerald-500/10 border border-emerald-500/20 rounded-full px-4 py-1.5 mb-8"
            style={{
              opacity: heroVisible ? 1 : 0,
              transform: heroVisible ? "translateY(0)" : "translateY(16px)",
              transition: "opacity 0.6s ease 0.1s, transform 0.6s ease 0.1s",
            }}
          >
            <span className="relative w-2 h-2 rounded-full bg-emerald-400 pulse-dot" />
            <span className="text-emerald-400 text-xs font-semibold uppercase tracking-widest">
              AI-Powered Finance Intelligence
            </span>
          </div>

          {/* Headline */}
          <h1
            className="font-display text-6xl md:text-8xl font-black leading-[0.9] tracking-tighter mb-6"
            style={{
              opacity: heroVisible ? 1 : 0,
              transform: heroVisible ? "translateY(0)" : "translateY(24px)",
              transition: "opacity 0.7s ease 0.2s, transform 0.7s ease 0.2s",
            }}
          >
            Master Your
            <br />
            <span className="text-gradient">Money,</span>
            <br />
            Effortlessly.
          </h1>

          {/* Subheadline */}
          <p
            className="text-slate-400 text-lg md:text-xl max-w-2xl mx-auto mb-10 leading-relaxed"
            style={{
              opacity: heroVisible ? 1 : 0,
              transform: heroVisible ? "translateY(0)" : "translateY(24px)",
              transition: "opacity 0.7s ease 0.35s, transform 0.7s ease 0.35s",
            }}
          >
            WalletIQ uses RAG-powered AI to turn your transactions into
            actionable financial insights — budgets, goals, and smart advice
            tailored to <em className="text-white not-italic">your</em>{" "}
            spending.
          </p>

          {/* CTA buttons */}
          <div
            className="flex flex-col sm:flex-row items-center justify-center gap-4"
            style={{
              opacity: heroVisible ? 1 : 0,
              transform: heroVisible ? "translateY(0)" : "translateY(24px)",
              transition: "opacity 0.7s ease 0.5s, transform 0.7s ease 0.5s",
            }}
          >
            <Link
              to="/signup"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-base px-8 py-4 rounded-2xl transition-all duration-200 cta-glow"
            >
              Start for free
              <span>→</span>
            </Link>
            <Link
              to="/login"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-white/[0.06] hover:bg-white/10 border border-white/10 text-white font-medium text-base px-8 py-4 rounded-2xl transition-all duration-200"
            >
              Sign in to dashboard
            </Link>
          </div>

          {/* Trust note */}
          <p
            className="text-slate-500 text-xs mt-6"
            style={{
              opacity: heroVisible ? 1 : 0,
              transition: "opacity 0.7s ease 0.65s",
            }}
          >
            No credit card required · Free forever plan available
          </p>
        </div>

        {/* Floating dashboard preview cards */}
        <div
          className="relative w-full max-w-5xl mx-auto px-6 mt-16"
          style={{
            opacity: heroVisible ? 1 : 0,
            transform: heroVisible ? "translateY(0)" : "translateY(40px)",
            transition: "opacity 0.9s ease 0.7s, transform 0.9s ease 0.7s",
          }}
        >
          <div className="grid grid-cols-3 gap-4">
            {/* Card 1 */}
            <div className="float col-span-1 bg-white/[0.04] border border-white/10 rounded-2xl p-5 backdrop-blur-sm">
              <div className="text-xs text-slate-500 uppercase tracking-widest mb-3">
                This Month
              </div>
              <div className="text-2xl font-display font-black text-white mb-1">
                ₹48,200
              </div>
              <div className="text-xs text-emerald-400 font-medium">
                ↑ 12% vs last month
              </div>
              <div className="mt-4 space-y-2">
                {[
                  ["Food & Dining", 34],
                  ["Transport", 18],
                  ["Shopping", 24],
                ].map(([label, pct]) => (
                  <div key={label as string}>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                      <span>{label}</span>
                      <span>{pct}%</span>
                    </div>
                    <div className="h-1 bg-white/5 rounded-full">
                      <div
                        className="h-1 bg-emerald-500 rounded-full"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Card 2 — center, taller */}
            <div className="float-delay col-span-1 bg-white/[0.06] border border-emerald-500/20 rounded-2xl p-5 backdrop-blur-sm -mt-6 shadow-2xl shadow-emerald-500/10">
              <div className="flex items-center justify-between mb-4">
                <span className="text-xs text-slate-500 uppercase tracking-widest">
                  AI Assistant
                </span>
                <span className="w-2 h-2 rounded-full bg-emerald-400 pulse-dot relative" />
              </div>
              <div className="space-y-3">
                <div className="bg-white/5 rounded-xl p-3">
                  <p className="text-xs text-slate-300">
                    "How much did I spend on food this month?"
                  </p>
                </div>
                <div className="bg-emerald-500/10 rounded-xl p-3 border border-emerald-500/20">
                  <p className="text-xs text-slate-200">
                    You spent{" "}
                    <span className="text-emerald-400 font-semibold">
                      ₹8,340
                    </span>{" "}
                    on Food & Dining — that's 17% of your budget. You're on
                    track! 🎯
                  </p>
                </div>
                <div className="flex items-center gap-2 mt-3">
                  <div className="flex-1 bg-white/5 rounded-lg px-3 py-2 text-xs text-slate-500">
                    Ask WalletIQ anything...
                  </div>
                  <div className="w-8 h-8 rounded-lg bg-emerald-500 flex items-center justify-center text-black text-xs font-bold">
                    →
                  </div>
                </div>
              </div>
            </div>

            {/* Card 3 */}
            <div className="float col-span-1 bg-white/[0.04] border border-white/10 rounded-2xl p-5 backdrop-blur-sm">
              <div className="text-xs text-slate-500 uppercase tracking-widest mb-3">
                Savings Goals
              </div>
              {[
                { label: "Emergency Fund", pct: 68, color: "bg-emerald-500" },
                { label: "MacBook Pro", pct: 42, color: "bg-cyan-500" },
                { label: "Vacation 2026", pct: 21, color: "bg-violet-500" },
              ].map(({ label, pct, color }) => (
                <div key={label} className="mb-3">
                  <div className="flex justify-between text-xs mb-1">
                    <span className="text-slate-300">{label}</span>
                    <span className="text-slate-400">{pct}%</span>
                  </div>
                  <div className="h-1.5 bg-white/5 rounded-full">
                    <div
                      className={`h-1.5 ${color} rounded-full transition-all`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                </div>
              ))}
              <div className="mt-4 text-xs text-slate-500">
                3 active goals · ₹1,24,000 target
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Stats ── */}
      <section
        ref={statsRef}
        className="py-24 border-y border-white/10 bg-white/[0.01]"
      >
        <div className="max-w-5xl mx-auto px-6 grid grid-cols-2 md:grid-cols-4 gap-12">
          <StatCard
            value={12000}
            suffix="+"
            label="Transactions tracked"
            inView={statsInView}
          />
          <StatCard
            value={98}
            suffix="%"
            label="Uptime SLA"
            inView={statsInView}
          />
          <StatCard
            value={5}
            suffix="x"
            label="Faster insights"
            inView={statsInView}
          />
          <StatCard
            value={500}
            suffix="+"
            label="Active users"
            inView={statsInView}
          />
        </div>
      </section>

      {/* ── Features ── */}
      <section id="features" className="py-28 max-w-7xl mx-auto px-6">
        <div className="text-center mb-16">
          <div className="inline-block text-emerald-400 text-xs font-semibold uppercase tracking-widest bg-emerald-500/10 border border-emerald-500/20 px-4 py-1.5 rounded-full mb-4">
            Everything you need
          </div>
          <h2 className="font-display text-4xl md:text-5xl font-black text-white">
            Built for serious
            <br />
            <span className="text-gradient">financial clarity</span>
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <FeatureCard
            icon="🤖"
            title="RAG-Powered AI Chat"
            desc="Ask anything about your finances in plain English. Our AI retrieves your actual transaction data to give grounded, accurate answers — no hallucinations."
            delay="0ms"
          />
          <FeatureCard
            icon="📊"
            title="Smart Dashboard"
            desc="Monthly income, expenses, net balance, category breakdowns, and daily trends — all at a glance. Beautiful charts, zero noise."
            delay="80ms"
          />
          <FeatureCard
            icon="💰"
            title="Budget Alerts"
            desc="Set spending limits per category. Get real-time alerts via WebSocket when you approach or exceed your budget threshold."
            delay="160ms"
          />
          <FeatureCard
            icon="🎯"
            title="Savings Goals"
            desc="Define goals with deadlines and target amounts. Track progress, add contributions, and watch your milestones fall."
            delay="0ms"
          />
          <FeatureCard
            icon="🔄"
            title="Recurring Transactions"
            desc="Schedule daily, weekly, monthly, or yearly transactions. Get a 30–365 day cash flow forecast so you're never surprised."
            delay="80ms"
          />
          <FeatureCard
            icon="📤"
            title="CSV Export"
            desc="Download your full transaction history as a UTF-8 CSV in one click. Excel-compatible with BOM encoding."
            delay="160ms"
          />
        </div>
      </section>

      {/* ── How it works ── */}
      <section
        id="how-it-works"
        className="py-28 bg-white/[0.015] border-y border-white/10"
      >
        <div className="max-w-5xl mx-auto px-6">
          <div className="grid md:grid-cols-2 gap-16 items-center">
            <div>
              <div className="inline-block text-cyan-400 text-xs font-semibold uppercase tracking-widest bg-cyan-500/10 border border-cyan-500/20 px-4 py-1.5 rounded-full mb-6">
                How it works
              </div>
              <h2 className="font-display text-4xl font-black text-white mb-12 leading-tight">
                From signup to
                <br />
                <span className="text-gradient">insight in minutes</span>
              </h2>

              <div className="space-y-8">
                <StepCard
                  num="01"
                  title="Create your account"
                  desc="Sign up free. Verify your email with a 6-digit OTP. No credit card needed."
                  delay="0ms"
                />
                <StepCard
                  num="02"
                  title="Add your transactions"
                  desc="Log income and expenses manually, or let recurring rules automate the routine ones."
                  delay="100ms"
                />
                <StepCard
                  num="03"
                  title="Set budgets & goals"
                  desc="Define monthly category budgets and long-term savings goals with deadlines."
                  delay="200ms"
                />
                <StepCard
                  num="04"
                  title="Ask your AI assistant"
                  desc="Chat with WalletIQ in natural language. It retrieves your real data to answer questions accurately."
                  delay="300ms"
                />
              </div>
            </div>

            {/* Terminal-style AI demo */}
            <div className="bg-[#0d1117] border border-white/10 rounded-2xl overflow-hidden shadow-2xl">
              <div className="flex items-center gap-2 px-4 py-3 border-b border-white/10">
                <div className="w-2.5 h-2.5 rounded-full bg-red-500/60" />
                <div className="w-2.5 h-2.5 rounded-full bg-yellow-500/60" />
                <div className="w-2.5 h-2.5 rounded-full bg-green-500/60" />
                <span className="text-xs text-slate-500 ml-2 font-mono">
                  WalletIQ Chat
                </span>
              </div>
              <div className="p-5 space-y-4 font-mono text-sm">
                {[
                  { role: "user", text: "Am I spending too much on food?" },
                  {
                    role: "ai",
                    text: "🍔 You've spent ₹6,240 on Food & Dining this month — 78% of your ₹8,000 budget.\n\n⚠️ At this rate you'll exceed by ~₹800.\n\n💡 Tip: Cut 2 restaurant visits and you'll stay on track.",
                  },
                  { role: "user", text: "What's my biggest expense category?" },
                  {
                    role: "ai",
                    text: "📦 Your top 3 this month:\n1. 🏠 Rent — ₹15,000\n2. 🍔 Food — ₹6,240\n3. 🚕 Transport — ₹2,100\n\nRent is expected. Food is near limit. Transport is fine! ✅",
                  },
                ].map((msg, i) => (
                  <div
                    key={i}
                    className={`flex gap-3 ${msg.role === "user" ? "justify-end" : ""}`}
                  >
                    {msg.role === "ai" && (
                      <div className="w-6 h-6 rounded-lg bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400 text-xs shrink-0 mt-0.5">
                        W
                      </div>
                    )}
                    <div
                      className={`max-w-[85%] text-xs leading-relaxed px-3 py-2.5 rounded-xl whitespace-pre-line ${
                        msg.role === "user"
                          ? "bg-white/10 text-slate-200"
                          : "bg-emerald-500/10 border border-emerald-500/20 text-slate-200"
                      }`}
                    >
                      {msg.text}
                    </div>
                    {msg.role === "user" && (
                      <div className="w-6 h-6 rounded-full bg-white/10 flex items-center justify-center text-white text-xs shrink-0 mt-0.5">
                        U
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Testimonials ── */}
      <section id="testimonials" className="py-28 max-w-7xl mx-auto px-6">
        <div className="text-center mb-16">
          <h2 className="font-display text-4xl md:text-5xl font-black text-white">
            What users are
            <br />
            <span className="text-gradient">saying</span>
          </h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Testimonial
            quote="Finally a finance app that actually answers my questions instead of showing me useless graphs. The AI chat is incredible."
            name="Arjun M."
            role="Software Engineer, Bangalore"
            delay="0ms"
          />
          <Testimonial
            quote="Budget alerts over WebSocket are a game-changer. I get notified the moment I'm close to my limit. No more end-of-month surprises."
            name="Priya S."
            role="Product Manager, Mumbai"
            delay="100ms"
          />
          <Testimonial
            quote="The recurring transaction forecast feature helped me plan my cash flow for the next 6 months. This is what modern finance looks like."
            name="Rahul K."
            role="Freelancer, Hyderabad"
            delay="200ms"
          />
        </div>
      </section>

      {/* ── CTA Banner ── */}
      <section className="py-24 px-6">
        <div className="max-w-3xl mx-auto text-center bg-gradient-to-br from-emerald-500/10 to-cyan-500/5 border border-emerald-500/20 rounded-3xl p-16">
          <h2 className="font-display text-4xl md:text-5xl font-black text-white mb-4 leading-tight">
            Start mastering
            <br />
            your money today.
          </h2>
          <p className="text-slate-400 mb-10">
            Join WalletIQ free. No credit card. Full access to AI chat, budgets,
            and goals.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              to="/signup"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-base px-8 py-4 rounded-2xl transition-all duration-200 cta-glow"
            >
              Create free account →
            </Link>
            <Link
              to="/login"
              className="w-full sm:w-auto text-slate-400 hover:text-white text-sm transition-colors duration-200"
            >
              Already have an account? Sign in
            </Link>
          </div>
        </div>
      </section>

      {/* ── Footer ── */}
      <footer className="border-t border-white/10 py-10 px-6">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded-lg bg-gradient-to-br from-emerald-400 to-cyan-500 flex items-center justify-center">
              <span className="text-black font-black text-xs">W</span>
            </div>
            <span className="font-display font-bold text-white">
              Wallet<span className="text-emerald-400">IQ</span>
            </span>
          </div>
          <p className="text-slate-500 text-sm">
            © 2026 WalletIQ. Built by Ripan Baidya · Apache 2.0
          </p>
          <div className="flex items-center gap-6 text-sm text-slate-500">
            <Link to="/login" className="hover:text-white transition-colors">
              Sign in
            </Link>
            <Link to="/signup" className="hover:text-white transition-colors">
              Sign up
            </Link>
            <a
              href="https://github.com/ripan-baidya/walletiq"
              target="_blank"
              rel="noreferrer"
              className="hover:text-white transition-colors"
            >
              GitHub
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
