import { TESTIMONIALS, type Testimonial } from "../../../shared/constants/homeData";

/* Card */
const TestimonialCard: React.FC<{ item: Testimonial; mobile?: boolean }> = ({
  item,
  mobile = false,
}) => {
  const initial = item.author.charAt(0).toUpperCase();

  return (
    <div
      className={`flex-shrink-0 bg-white/5 border border-white/10 rounded-2xl hover:bg-white/[0.08] transition-colors ${
        mobile
          ? "w-full p-4"
          : "w-64 sm:w-72 p-5 sm:p-6 mx-2 sm:mx-3"
      }`}
    >
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
    className="py-16 sm:py-24 bg-[#0f0f0f] border-y border-white/5 overflow-x-hidden"
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
      .marquee-row-mobile   { animation: marquee-ltr 52s linear infinite; }

      /* Slightly faster on desktop, but still slow */
      @media (min-width: 640px) {
        .marquee-row,
        .marquee-row-reversed { animation-duration: 68s; }
      }

      /* Respect reduced motion */
      @media (prefers-reduced-motion: reduce) {
        .marquee-row,
        .marquee-row-reversed,
        .marquee-row-mobile { animation-play-state: paused; }
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

    {/* Mobile: auto-moving marquee */}
    <div className="sm:hidden overflow-hidden">
      <div className="marquee-row-mobile flex w-max">
        {[...TESTIMONIALS, ...TESTIMONIALS].map((item, index) => (
          <div key={`${item.author}-${index}`} className="w-[84vw] max-w-[20rem] mx-2 first:ml-4 last:mr-4">
            <TestimonialCard item={item} mobile />
          </div>
        ))}
      </div>
    </div>

    {/* Desktop/tablet: marquee rows */}
    <div className="hidden sm:block">
      <MarqueeRow items={TESTIMONIALS} />
      <div className="h-3 sm:h-4" />
      <MarqueeRow items={[...TESTIMONIALS].reverse()} reversed />
    </div>
  </section>
);

export default TestimonialsSection;
