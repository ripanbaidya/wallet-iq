import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";
import { useAppQuery } from "../../hooks/useAppQuery";
import { aboutService } from "../../services/aboutService";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";

// ── Row ───────────────────────────────────────────────────────────────────────

const Row: React.FC<{ label: string; value: React.ReactNode }> = ({
  label,
  value,
}) => (
  <div className="flex items-center justify-between py-3.5 gap-4 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500 shrink-0">{label}</span>
    <span className="text-sm font-medium text-gray-900 text-right break-all min-w-0">
      {value}
    </span>
  </div>
);

// ── Section ───────────────────────────────────────────────────────────────────

const Section: React.FC<{ title: string; children: React.ReactNode }> = ({
  title,
  children,
}) => (
  <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
    <div className="px-4 sm:px-5 pt-4 pb-2 border-b border-gray-100">
      <p className="text-xs font-semibold uppercase tracking-widest text-gray-400">
        {title}
      </p>
    </div>
    <div className="px-4 sm:px-5">{children}</div>
  </div>
);

// ── Page ──────────────────────────────────────────────────────────────────────

export default function AboutPage() {
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["app-info"],
    queryFn: () => aboutService.getAppInfo(),
    staleTime: 1000 * 60 * 10,
  });

  const info = data?.data;

  if (isLoading) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  if (error || !info) {
    return <QueryError error={error ?? null} onRetry={refetch} />;
  }

  return (
    <div className="max-w-xl mx-auto px-4 sm:px-6 lg:px-0 py-2 sm:py-4 space-y-3 sm:space-y-4">
      {/* Page heading */}
      <div>
        <h1 className="text-xl font-semibold text-gray-900">About</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Application information, support details and links.
        </p>
      </div>

      {/* Hero card */}
      <div className="bg-[#0f0f0f] rounded-2xl px-5 sm:px-8 py-8 sm:py-10 text-center relative overflow-hidden">
        {/* Dot grid */}
        <div
          className="absolute inset-0 pointer-events-none opacity-60"
          style={{
            backgroundImage:
              "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
            backgroundSize: "24px 24px",
          }}
        />
        {/* Accent glow */}
        <div className="absolute -top-8 left-1/2 -translate-x-1/2 w-48 h-48 bg-[#e8ff4f]/10 rounded-full blur-3xl pointer-events-none" />

        <div className="relative">
          {/* App name */}
          <h2 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
            {info.name.slice(0, -2)}
            <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
              {info.name.slice(-2)}
            </span>
          </h2>

          <p className="text-white/40 text-xs sm:text-sm mt-3 max-w-xs sm:max-w-md mx-auto leading-relaxed">
            An AI-powered personal finance system that turns your transaction
            data into actionable financial intelligence.
          </p>

          {/* Version badges */}
          <div className="flex justify-center gap-2 mt-5 flex-wrap">
            <span className="text-xs bg-[#e8ff4f]/10 text-[#e8ff4f] border border-[#e8ff4f]/20 px-3 py-1 rounded-full font-medium">
              v{info.version}
            </span>
            <span className="text-xs bg-white/5 text-white/50 border border-white/10 px-3 py-1 rounded-full font-medium">
              Build {info.buildNumber}
            </span>
          </div>
        </div>
      </div>

      {/* License */}
      <Section title="License">
        <Row
          label={info.license.name}
          value={
            <a
              href={info.license.url}
              target="_blank"
              rel="noreferrer"
              className="text-gray-500 hover:text-black transition-colors inline-flex items-center gap-1"
            >
              View
              <svg
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <line x1="7" y1="17" x2="17" y2="7" />
                <polyline points="7 7 17 7 17 17" />
              </svg>
            </a>
          }
        />
      </Section>

      {/* Support */}
      <Section title="Support">
        <Row
          label="Email"
          value={
            <a
              href={`mailto:${info.support.email}`}
              className="text-gray-500 hover:text-black transition-colors break-all"
            >
              {info.support.email}
            </a>
          }
        />
        <Row label="Working hours" value={info.support.workingHours} />
      </Section>

      {/* Social links */}
      <Section title="Connect">
        <div className="flex justify-center items-center gap-5 sm:gap-8 py-4">
          {/* GitHub — href disabled until backend serves it */}
          <a
            // href={info.social.github}
            target="_blank"
            rel="noreferrer"
            title="GitHub"
            className="group flex flex-col items-center gap-1.5 text-gray-400 hover:text-gray-900 transition-all cursor-pointer"
          >
            <span className="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 group-hover:border-gray-900 group-hover:bg-gray-900 group-hover:text-white transition-all">
              <FaGithub size={18} />
            </span>
            <span className="text-[10px] text-gray-400 group-hover:text-gray-700">
              GitHub
            </span>
          </a>

          {/*
            Note: LinkedIn and Instagram URLs are not serving from the Backend API yet.
            Hardcoded for now — will be replaced once the backend is fixed.
          */}
          <a
            // href={info.social.linkedIn}
            href="https://www.linkedin.com/in/ripanbaidya/"
            target="_blank"
            rel="noreferrer"
            title="LinkedIn"
            className="group flex flex-col items-center gap-1.5 text-gray-400 hover:text-blue-600 transition-all"
          >
            <span className="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 group-hover:border-blue-200 group-hover:bg-blue-50 transition-all">
              <FaLinkedin size={18} />
            </span>
            <span className="text-[10px] text-gray-400 group-hover:text-blue-500">
              LinkedIn
            </span>
          </a>

          <a
            // href={info.social.instagram}
            href="https://www.instagram.com/futurenoogler"
            target="_blank"
            rel="noreferrer"
            title="Instagram"
            className="group flex flex-col items-center gap-1.5 text-gray-400 hover:text-pink-500 transition-all"
          >
            <span className="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 group-hover:border-pink-200 group-hover:bg-pink-50 transition-all">
              <FaInstagram size={18} />
            </span>
            <span className="text-[10px] text-gray-400 group-hover:text-pink-500">
              Instagram
            </span>
          </a>
        </div>
      </Section>

      {/* Footer */}
      <div className="pt-2 pb-4">
        <p className="text-xs text-gray-400 text-center">{info.copyright}</p>
      </div>
    </div>
  );
}
