import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";
import { useAppQuery } from "../../hooks/useAppQuery";
import { aboutService } from "../../services/aboutService";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";

/**
 * Row — clean inline data row
 */
const Row: React.FC<{ label: string; value: React.ReactNode }> = ({
  label,
  value,
}) => (
  <div className="flex items-center justify-between py-3 gap-4">
    <span className="text-sm text-gray-500">{label}</span>
    <span className="text-sm font-medium text-gray-900 text-right break-all">
      {value}
    </span>
  </div>
);

/**
 * Section — lightweight divider-based layout (replaces cards)
 */
const Section: React.FC<{ title: string; children: React.ReactNode }> = ({
  title,
  children,
}) => (
  <div className="space-y-3">
    <p className="text-xs text-gray-400 uppercase tracking-widest">{title}</p>
    <div className="divide-y divide-gray-100">{children}</div>
  </div>
);

/**
 * About Page
 */
export default function AboutPage() {
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["app-info"],
    queryFn: () => aboutService.getAppInfo(),
    staleTime: 1000 * 60 * 10,
  });

  const info = data?.data;

  // Loading
  if (isLoading) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  // Error
  if (error || !info) {
    return <QueryError error={error ?? null} onRetry={refetch} />;
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-xl font-semibold text-gray-900">About</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Application information, support details and links.
        </p>
      </div>

      {/* Hero */}
      <div className="bg-[#0f0f0f] rounded-xl px-6 py-10 text-center relative overflow-hidden">
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            backgroundImage:
              "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
            backgroundSize: "24px 24px",
          }}
        />

        <div className="relative">
          <h2 className="text-3xl font-black tracking-tight text-white">
            {info.name.slice(0, -2)}
            <span className="text-[#e8ff4f]">{info.name.slice(-2)}</span>
          </h2>

          <p className="text-white/40 text-sm mt-3 max-w-md mx-auto">
            An AI-powered personal finance system that turns your transaction
            data into actionable financial intelligence.
          </p>

          <div className="flex justify-center gap-2 mt-5 flex-wrap">
            <span className="text-xs bg-[#e8ff4f]/10 text-[#e8ff4f] px-3 py-1 rounded-full">
              v{info.version}
            </span>
            <span className="text-xs bg-white/5 text-white/50 px-3 py-1 rounded-full">
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
              className="hover:text-black transition-colors"
            >
              View →
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
              className="hover:text-black transition-colors"
            >
              {info.support.email}
            </a>
          }
        />
        <Row label="Working hours" value={info.support.workingHours} />
      </Section>

      {/* Links */}
      <Section title="Links">
        <div className="flex justify-center gap-6 py-4 text-xl text-gray-400">
          <a
            // href={info.social.github}
            target="_blank"
            rel="noreferrer"
            className="transition-all cursor-pointer hover:text-black hover:scale-110"
          >
            <FaGithub />
          </a>

          {/* 
          Note: LinkedIn and Instagram URL is not rendering from the Backend API's
          I have Hardcoded the URL Here, and will fix it in the Future.
          */}
          <a
            // href={info.social.linkedIn as string}
            href="https://www.linkedin.com/in/ripanbaidya/"
            target="_blank"
            rel="noreferrer"
            className="transition-all cursor-pointer hover:text-blue-500 hover:scale-110"
          >
            <FaLinkedin />
          </a>

          <a
            // href={info.social.instagram as string}
            href="https://www.instagram.com/fnoogler"
            target="_blank"
            rel="noreferrer"
            className="transition-all cursor-pointer hover:text-pink-500 hover:scale-110"
          >
            <FaInstagram />
          </a>
        </div>
      </Section>

      {/* Footer */}
      <div className="pt-6 border-t border-gray-100">
        <p className="text-xs text-gray-400 text-center">{info.copyright}</p>
      </div>
    </div>
  );
}
