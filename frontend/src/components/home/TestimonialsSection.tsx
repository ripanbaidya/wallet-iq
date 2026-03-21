import { TESTIMONIALS, type Testimonial } from "../../data/homeData";

/* Card */
const TestimonialCard: React.FC<{ item: Testimonial }> = ({ item }) => {
  const initial = item.author.charAt(0).toUpperCase();

  return (
    <div className="flex-shrink-0 w-64 sm:w-72 bg-white/5 border border-white/10 rounded-2xl p-5 sm:p-6 mx-2 sm:mx-3 hover:bg-white/[0.08] transition-colors">
      {/* Opening quote */}
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
            className="w-8 h-8 sm:w-9 sm:h-9 rounded-full object-cover border border-white/10 shrink-0"
          />
        ) : (
          <div className="w-8 h-8 sm:w-9 sm:h-9 rounded-full bg-[#e8ff4f] flex items-center justify-center text-gray-900 text-sm font-bold shrink-0">
            {initial}
          </div>
        )}
        <div className="min-w-0">
          <p className="text-sm font-semibold text-white truncate">
            {item.author}
          </p>
          <p className="text-xs text-white/40 truncate">{item.role}</p>
        </div>
      </div>
    </div>
  );
};

/* Marquee row */
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

/* Section */
const TestimonialsSection: React.FC = () => (
  <section
    id="testimonials"
    className="py-16 sm:py-24 bg-[#0f0f0f] border-y border-white/5 overflow-hidden"
  >
    <style>{`
      @keyframes marquee-ltr {
        0%   { transform: translateX(0); }
        100% { transform: translateX(-50%); }
      }

      @keyframes marquee-rtl {
        0%   { transform: translateX(-50%); }
        100% { transform: translateX(0); }
      }

      /* Slower overall */
      .marquee-row          { animation: marquee-ltr 80s linear infinite; }
      .marquee-row-reversed { animation: marquee-rtl 80s linear infinite; }

      /* Slightly faster on desktop, but still slow */
      @media (min-width: 640px) {
        .marquee-row,
        .marquee-row-reversed { animation-duration: 68s; }
      }

      /* Respect reduced motion */
      @media (prefers-reduced-motion: reduce) {
        .marquee-row,
        .marquee-row-reversed { animation-play-state: paused; }
      }

      /* Pause on hover */
      .marquee-row:hover,
      .marquee-row-reversed:hover {
        animation-play-state: paused;
      }
    `}</style>

    {/* Section header */}
    <div className="max-w-5xl mx-auto px-4 sm:px-6 mb-10 sm:mb-12">
      <p className="text-xs text-white/30 uppercase tracking-widest mb-3">
        What people say
      </p>
      <h2 className="text-3xl sm:text-4xl md:text-5xl font-black tracking-tight text-white leading-[1.05]">
        Trusted by users
        <br />
        <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
          across India.
        </span>
      </h2>
    </div>

    {/* Row 1 — left to right */}
    <MarqueeRow items={TESTIMONIALS} />

    <div className="h-3 sm:h-4" />

    {/* Row 2 — right to left */}
    <MarqueeRow items={[...TESTIMONIALS].reverse()} reversed />
  </section>
);

export default TestimonialsSection;
