import { TESTIMONIALS, type Testimonial } from "../../data/homeData";

/**
 * Single testimonial card — white card on dark background
 */
const TestimonialCard: React.FC<{ item: Testimonial }> = ({ item }) => {
  const initial = item.author.charAt(0).toUpperCase();

  return (
    <div className="flex-shrink-0 w-72 bg-white/5 border border-white/10 rounded-2xl p-6 mx-3 hover:bg-white/8 transition-colors">
      {/* Opening quote — accent colour */}
      <span className="text-3xl text-[#e8ff4f] font-serif leading-none select-none">
        &ldquo;
      </span>

      <p className="text-sm text-white/70 leading-relaxed mt-1 mb-5">
        {item.quote}
      </p>

      {/* Author */}
      <div className="flex items-center gap-3">
        {item.avatarUrl ? (
          <img
            src={item.avatarUrl}
            alt={item.author}
            className="w-9 h-9 rounded-full object-cover border border-white/10"
          />
        ) : (
          /* Fallback — accent yellow initial */
          <div className="w-9 h-9 rounded-full bg-[#e8ff4f] flex items-center justify-center text-gray-900 text-sm font-bold shrink-0">
            {initial}
          </div>
        )}
        <div>
          <p className="text-sm font-semibold text-white">{item.author}</p>
          <p className="text-xs text-white/40">{item.role}</p>
        </div>
      </div>
    </div>
  );
};

/**
 * MarqueeRow — duplicates items for a seamless infinite loop.
 * `reversed` plays the animation in the opposite direction.
 */
const MarqueeRow: React.FC<{ items: Testimonial[]; reversed?: boolean }> = ({
  items,
  reversed = false,
}) => {
  const doubled = [...items, ...items];

  return (
    <div className="overflow-hidden">
      <div
        className={reversed ? "marquee-row-reversed" : "marquee-row"}
        style={{ display: "flex", width: "max-content" }}
      >
        {doubled.map((item, i) => (
          <TestimonialCard key={`${item.author}-${i}`} item={item} />
        ))}
      </div>
    </div>
  );
};

/**
 * TestimonialsSection
 * Dark background (#0f0f0f) — second dark band, mirrors the hero.
 * Two marquee rows scroll in opposite directions.
 * Cards pause on hover for readability.
 */
const TestimonialsSection: React.FC = () => (
  <section
    id="testimonials"
    className="py-24 bg-[#0f0f0f] border-y border-white/5 overflow-hidden"
  >
    {/* Keyframes injected once — scoped to this section */}
    <style>{`
      @keyframes marquee-ltr {
        0%   { transform: translateX(0); }
        100% { transform: translateX(-50%); }
      }
      @keyframes marquee-rtl {
        0%   { transform: translateX(-50%); }
        100% { transform: translateX(0); }
      }
      .marquee-row          { animation: marquee-ltr 36s linear infinite; }
      .marquee-row-reversed { animation: marquee-rtl 36s linear infinite; }
      .marquee-row:hover,
      .marquee-row-reversed:hover { animation-play-state: paused; }
    `}</style>

    {/* Section header */}
    <div className="max-w-5xl mx-auto px-6 mb-12">
      <p className="text-xs text-white/30 uppercase tracking-widest mb-3">
        What people say
      </p>
      <h2 className="text-4xl md:text-5xl font-black tracking-tight text-white leading-[1.05]">
        Trusted by real users
        <br />
        <span className="text-[#e8ff4f]">across India.</span>
      </h2>
    </div>

    {/* Row 1 — left to right */}
    <MarqueeRow items={TESTIMONIALS} />

    <div className="h-4" />

    {/* Row 2 — right to left */}
    <MarqueeRow items={[...TESTIMONIALS].reverse()} reversed />
  </section>
);

export default TestimonialsSection;
