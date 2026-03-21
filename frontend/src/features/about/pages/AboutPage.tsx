import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { aboutService } from "../aboutService";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";

import AppInfoHero from "../components/AppInfoHero";
import AppInfoSection from "../components/AppInfoSection";
import AppInfoRow from "../components/AppInfoRow";
import SocialLinks from "../components/SocialLinks";

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

      <AppInfoHero
        name={info.name}
        version={info.version}
        buildNumber={info.buildNumber}
      />

      <AppInfoSection title="License">
        <AppInfoRow
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
      </AppInfoSection>

      <AppInfoSection title="Support">
        <AppInfoRow
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
        <AppInfoRow label="Working hours" value={info.support.workingHours} />
      </AppInfoSection>

      <AppInfoSection title="Connect">
        <SocialLinks social={info.social} />
      </AppInfoSection>

      {/* Footer */}
      <div className="pt-2 pb-4">
        <p className="text-xs text-gray-400 text-center">{info.copyright}</p>
      </div>
    </div>
  );
}
