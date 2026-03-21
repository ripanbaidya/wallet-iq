import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

// Typewriter phrases cycling below the headline
const PHRASES = [
  "Track every rupee. Miss nothing.",
  "Ask your AI. Get real answers.",
  "Stay in control of every expense.",
  "Reach your savings goals—faster.",
  "Know exactly where your money goes.",
  "Smarter decisions, powered by AI.",
  "No surprises at month-end.",
  "All your finances. One place.",
];

const HeroSection: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [phraseIndex, setPhraseIndex] = useState(0);
  const [displayed, setDisplayed] = useState("");
  const [deleting, setDeleting] = useState(false);
  const [charIndex, setCharIndex] = useState(0);

  // Fade-in on mount
  useEffect(() => {
    const t = setTimeout(() => setVisible(true), 80);
    return () => clearTimeout(t);
  }, []);

  // Typewriter
  useEffect(() => {
    const current = PHRASES[phraseIndex];
    if (!deleting && charIndex === current.length) {
      const t = setTimeout(() => setDeleting(true), 1600);
      return () => clearTimeout(t);
    }
    if (deleting && charIndex === 0) {
      setDeleting(false);
      setPhraseIndex((i) => (i + 1) % PHRASES.length);
      return;
    }
    const t = setTimeout(
      () => {
        setCharIndex((c) => c + (deleting ? -1 : 1));
        setDisplayed(current.slice(0, charIndex));
      },
      deleting ? 25 : 46,
    );
    return () => clearTimeout(t);
  }, [charIndex, deleting, phraseIndex]);

  return (
    <section className="relative min-h-screen flex flex-col items-center justify-center pt-16 px-6 overflow-hidden bg-[#0f0f0f]">
      {/* Subtle radial highlight behind the heading */}
      <div
        className="absolute top-0 left-1/2 -translate-x-1/2 w-[700px] h-[400px] pointer-events-none"
        style={{
          background:
            "radial-gradient(ellipse at top, rgba(232,255,79,0.07) 0%, transparent 70%)",
        }}
      />

      {/* Fine dot grid */}
      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          backgroundImage:
            "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
          backgroundSize: "30px 30px",
        }}
      />

      {/* Bottom fade to next section (dark → dark is fine, creates depth) */}
      <div className="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-[#0f0f0f] to-transparent pointer-events-none" />

      {/* Content */}
      <div
        className="relative max-w-3xl w-full text-center"
        style={{
          opacity: visible ? 1 : 0,
          transform: visible ? "translateY(0)" : "translateY(24px)",
          transition: "opacity 0.8s ease, transform 0.8s ease",
        }}
      >
        {/* Eyebrow pill */}
        <span className="inline-flex items-center gap-2 text-xs font-medium text-white/50 border border-white/10 rounded-full px-3 py-1 mb-8">
          <span className="w-1.5 h-1.5 rounded-full bg-[#e8ff4f] animate-pulse" />
          AI-powered personal finance
        </span>

        {/* Main headline */}
        <h1 className="text-5xl sm:text-6xl md:text-[5.5rem] font-black tracking-tight text-white leading-[1.0] mb-6">
          Your money,
          <br />
          finally under
          <br />
          {/* Single accent word — not the whole line */}
          <span className="text-[#e8ff4f]">control.</span>
        </h1>

        {/* Typewriter subline */}
        <p className="text-base text-white/40 h-7 mb-10 font-mono tracking-tight">
          {displayed}
          <span className="text-[#e8ff4f] animate-pulse">|</span>
        </p>

        {/* CTAs */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
          <Link
            to="/signup"
            className="w-full sm:w-auto text-sm font-bold bg-[#e8ff4f] text-gray-900 px-8 py-3.5 rounded-xl hover:bg-[#d4eb3a] transition-colors"
          >
            Start for free →
          </Link>
          <Link
            to="/login"
            className="w-full sm:w-auto text-sm text-white/60 border border-white/10 px-8 py-3.5 rounded-xl hover:bg-white/5 hover:text-white transition-colors"
          >
            Sign in to my account
          </Link>
        </div>

        <p className="text-xs text-white/25 mt-5">
          No credit card · Free forever plan available
        </p>
      </div>

      {/* Scroll cue */}
      <div
        className="absolute bottom-10 left-1/2 -translate-x-1/2 flex flex-col items-center gap-1 text-white/20"
        style={{ opacity: visible ? 1 : 0, transition: "opacity 1s ease 1.4s" }}
      >
        <span className="text-[10px] tracking-widest uppercase">scroll</span>
        <span className="text-base animate-bounce">↓</span>
      </div>
    </section>
  );
};

export default HeroSection;
