import { useRef } from "react";
import { STATS } from "../../data/homeData";
import { useCounter, useInView } from "../../utils/animationHooks";

/**
 * StatCell — animates from 0 to target when enabled
 */
const StatCell: React.FC<{
  value: number;
  suffix: string;
  label: string;
  enabled: boolean;
}> = ({ value, suffix, label, enabled }) => {
  const count = useCounter(value, 1800, enabled);
  return (
    <div className="text-center">
      {/* Number in accent yellow so it pops on dark bg */}
      <div className="text-4xl font-black tracking-tight text-[#e8ff4f]">
        {count.toLocaleString("en-IN")}
        <span className="text-white/40 text-2xl">{suffix}</span>
      </div>
      <div className="text-xs text-white/40 mt-2 uppercase tracking-widest">
        {label}
      </div>
    </div>
  );
};

/**
 * StatsSection
 * Dark band (#111) that creates a strong visual break between the hero
 * and the white Features section below.
 */
const StatsSection: React.FC = () => {
  const containerRef = useRef<HTMLElement>(null);
  const { ref, inView } = useInView<HTMLElement>(0.3);

  return (
    <section
      ref={(node) => {
        (ref as React.MutableRefObject<HTMLElement | null>).current = node;
        (
          containerRef as React.MutableRefObject<HTMLElement | null>
        ).current = node;
      }}
      className="bg-[#111] border-y border-white/5 py-16"
    >
      <div className="max-w-4xl mx-auto px-6 grid grid-cols-2 md:grid-cols-4 gap-10">
        {STATS.map((stat) => (
          <StatCell
            key={stat.label}
            value={stat.value}
            suffix={stat.suffix}
            label={stat.label}
            enabled={inView}
          />
        ))}
      </div>
    </section>
  );
};

export default StatsSection;
