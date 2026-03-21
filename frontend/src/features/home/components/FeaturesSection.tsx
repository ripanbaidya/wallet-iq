import { FEATURES } from "../../../shared/constants/homeData";

const FeatureCard: React.FC<{
  icon: string;
  title: string;
  description: string;
  index: number;
}> = ({ icon, title, description, index }) => (
  <div className="group relative bg-white border-b border-r border-gray-100 p-6 sm:p-7 hover:bg-[#0f0f0f] transition-colors duration-300 overflow-hidden">
    {/* Accent glow — appears on hover */}
    <div className="absolute -top-10 -right-10 w-32 h-32 bg-[#e8ff4f]/0 group-hover:bg-[#e8ff4f]/10 rounded-full blur-2xl transition-all duration-500 pointer-events-none" />

    {/* Index number — top right, large ghost text */}
    <span className="absolute top-4 right-5 text-[11px] font-black tabular-nums text-gray-100 group-hover:text-white/10 transition-colors duration-300 select-none">
      {String(index + 1).padStart(2, "0")}
    </span>

    {/* Icon */}
    <div className="w-10 h-10 rounded-xl bg-gray-50 group-hover:bg-white/5 border border-gray-100 group-hover:border-white/10 flex items-center justify-center text-xl transition-all duration-300 mb-5">
      {icon}
    </div>

    {/* Title */}
    <h3 className="text-sm font-bold text-gray-900 group-hover:text-white transition-colors duration-300 mb-2 pr-6">
      {title}
    </h3>

    {/* Description */}
    <p className="text-sm text-gray-500 group-hover:text-white/50 leading-relaxed transition-colors duration-300">
      {description}
    </p>

    {/* Accent bottom line — slides in on hover */}
    <div className="absolute bottom-0 left-0 h-0.5 w-0 group-hover:w-full bg-[#e8ff4f] transition-all duration-500 rounded-full" />
  </div>
);

// Section
const FeaturesSection: React.FC = () => (
  <section id="features" className="py-16 sm:py-24 bg-white">
    <div className="max-w-5xl mx-auto px-4 sm:px-6">
      {/* Section header */}
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-10 sm:mb-14">
        <div>
          <p className="text-xs text-gray-400 uppercase tracking-widest mb-3">
            What you get
          </p>
          <h2 className="text-3xl sm:text-4xl md:text-5xl font-black tracking-tight text-gray-900 leading-[1.05]">
            Everything in
            <br />
            <span className="text-gray-300 italic">one place.</span>
          </h2>
        </div>

        {/* Feature count pill — desktop only */}
        <div className="hidden sm:flex items-center gap-2 self-end mb-1">
          <span className="text-4xl font-black text-gray-100 tabular-nums leading-none select-none">
            {String(FEATURES.length).padStart(2, "0")}
          </span>
          <span className="text-xs text-gray-400 leading-tight">
            core
            <br />
            features
          </span>
        </div>
      </div>

      {/* Grid
          Mobile  : 1 column
          sm      : 2 columns
          lg      : 3 columns
          The outer border + gap-px trick creates hairline dividers between cells.
      */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-px bg-gray-100 border border-gray-100 rounded-2xl overflow-hidden">
        {FEATURES.map((f, i) => (
          <FeatureCard
            key={f.title}
            icon={f.icon}
            title={f.title}
            description={f.description}
            index={i}
          />
        ))}
      </div>

      {/* Bottom caption */}
      <p className="mt-6 text-xs text-gray-400 text-center sm:text-right">
        All features included — no tiers, no paywalls.
      </p>
    </div>
  </section>
);

export default FeaturesSection;
