import { FEATURES } from "../../data/homeData";

/**
 * FeaturesSection
 * White background — first light section after the dark hero + stats band.
 * Grid divided by 1px gray lines (no heavy card shadows).
 * Accent yellow used only on the hover border highlight.
 */
const FeaturesSection: React.FC = () => (
  <section id="features" className="py-24 px-6 bg-white">
    <div className="max-w-5xl mx-auto">
      {/* Section header */}
      <div className="mb-14">
        <p className="text-xs text-gray-400 uppercase tracking-widest mb-3">
          What you get
        </p>
        <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 leading-[1.05]">
          Everything in
          <br />
          <span className="text-gray-300 italic">one place.</span>
        </h2>
      </div>

      {/* 3-column grid separated by hairlines */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-px bg-gray-100 border border-gray-100 rounded-2xl overflow-hidden">
        {FEATURES.map((f) => (
          <div
            key={f.title}
            className="bg-white p-7 group hover:bg-gray-50 transition-colors duration-200 relative"
          >
            {/* Accent left border on hover */}
            <div className="absolute left-0 top-6 bottom-6 w-0.5 bg-[#e8ff4f] opacity-0 group-hover:opacity-100 transition-opacity duration-200 rounded-full" />

            <span className="text-2xl">{f.icon}</span>
            <h3 className="mt-4 text-sm font-semibold text-gray-900">
              {f.title}
            </h3>
            <p className="mt-1.5 text-sm text-gray-500 leading-relaxed">
              {f.description}
            </p>
          </div>
        ))}
      </div>
    </div>
  </section>
);

export default FeaturesSection;
