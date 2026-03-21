interface Props {
  name: string;
  version: string;
  buildNumber: string;
}

const AppInfoHero: React.FC<Props> = ({ name, version, buildNumber }) => (
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
      {/* App name — last 2 chars get accent colour */}
      <h2 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
        {name.slice(0, -2)}
        <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
          {name.slice(-2)}
        </span>
      </h2>

      <p className="text-white/40 text-xs sm:text-sm mt-3 max-w-xs sm:max-w-md mx-auto leading-relaxed">
        An AI-powered personal finance system that turns your transaction data
        into actionable financial intelligence.
      </p>

      {/* Version badges */}
      <div className="flex justify-center gap-2 mt-5 flex-wrap">
        <span className="text-xs bg-[#e8ff4f]/10 text-[#e8ff4f] border border-[#e8ff4f]/20 px-3 py-1 rounded-full font-medium">
          v{version}
        </span>
        <span className="text-xs bg-white/5 text-white/50 border border-white/10 px-3 py-1 rounded-full font-medium">
          Build {buildNumber}
        </span>
      </div>
    </div>
  </div>
);

export default AppInfoHero;
