import { STEPS } from "../../../shared/constants/homeData";

// Static AI demo conversation
const DEMO_CHAT = [
  { role: "user" as const, text: "How much did I spend on food this month?" },
  {
    role: "ai" as const,
    text: "You've spent ₹6,240 on Food & Dining — 78% of your ₹8,000 budget. At this pace you'll exceed by ~₹800. Consider skipping two restaurant visits this week.",
  },
  { role: "user" as const, text: "What's my biggest expense category?" },
  {
    role: "ai" as const,
    text: "Your top 3 this month: Rent ₹15,000 · Food ₹6,240 · Transport ₹2,100. Rent is expected. Food is near its limit. Transport looks fine.",
  },
];

/**
 * HowItWorksSection
 * Warm off-white (#f7f6f2) to break the monotony of pure white.
 * Step numbers use the accent yellow background — clear and distinct.
 * Chat panel mirrors the actual ChatPage aesthetic.
 */
const HowItWorksSection: React.FC = () => (
  <section
    id="how-it-works"
    className="py-24 px-6 bg-[#f7f6f2] border-y border-gray-200"
  >
    <div className="max-w-5xl mx-auto grid md:grid-cols-2 gap-16 items-start">
      {/* Left — numbered steps */}
      <div>
        <p className="text-xs text-gray-400 uppercase tracking-widest mb-3">
          How it works
        </p>
        <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 leading-[1.05] mb-12">
          Up and running
          <br />
          <span className="text-gray-400 italic">in minutes.</span>
        </h2>

        <div className="space-y-8">
          {STEPS.map((step, i) => (
            <div key={step.number} className="flex gap-5">
              {/* Step badge — accent yellow */}
              <div className="shrink-0 w-10 h-10 rounded-xl bg-[#e8ff4f] flex items-center justify-center">
                <span className="text-gray-900 text-xs font-black">
                  {step.number}
                </span>
              </div>

              <div className="min-w-0 pt-1.5">
                <h3 className="text-sm font-semibold text-gray-900 mb-1">
                  {step.title}
                </h3>
                <p className="text-sm text-gray-500 leading-relaxed">
                  {step.description}
                </p>
                {/* Connector line between steps */}
                {i < STEPS.length - 1 && (
                  <div className="mt-6 w-px h-6 bg-gray-300 ml-[-2.9rem] translate-x-5" />
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Right — mock AI chat panel */}
      <div className="rounded-2xl border border-gray-200 bg-white overflow-hidden shadow-sm">
        {/* Window chrome */}
        <div className="flex items-center gap-1.5 px-4 py-3 border-b border-gray-100 bg-gray-50">
          <span className="w-2.5 h-2.5 rounded-full bg-red-400/70" />
          <span className="w-2.5 h-2.5 rounded-full bg-yellow-400/70" />
          <span className="w-2.5 h-2.5 rounded-full bg-green-400/70" />
          <span className="ml-3 text-xs text-gray-400 font-mono">
            WalletIQ · AI Chat
          </span>
        </div>

        <div className="p-5 space-y-4 text-sm">
          {DEMO_CHAT.map((msg, i) => (
            <div
              key={i}
              className={`flex gap-3 ${msg.role === "user" ? "justify-end" : ""}`}
            >
              {msg.role === "ai" && (
                /* AI avatar — dark circle */
                <div className="w-7 h-7 rounded-lg bg-gray-900 flex items-center justify-center shrink-0 mt-0.5">
                  <span className="text-[#e8ff4f] text-xs font-bold">W</span>
                </div>
              )}
              <div
                className={`max-w-[82%] px-3 py-2.5 rounded-xl text-xs leading-relaxed ${
                  msg.role === "user"
                    ? "bg-gray-900 text-white rounded-br-sm"
                    : "bg-gray-100 text-gray-800 rounded-bl-sm"
                }`}
              >
                {msg.text}
              </div>
              {msg.role === "user" && (
                <div className="w-7 h-7 rounded-full bg-gray-100 flex items-center justify-center shrink-0 mt-0.5 text-xs text-gray-600 font-bold">
                  U
                </div>
              )}
            </div>
          ))}

          {/* Typing indicator */}
          <div className="flex gap-3">
            <div className="w-7 h-7 rounded-lg bg-gray-900 flex items-center justify-center shrink-0">
              <span className="text-[#e8ff4f] text-xs font-bold">W</span>
            </div>
            <div className="bg-gray-100 rounded-xl rounded-bl-sm px-4 py-3">
              <div className="flex gap-1 items-center">
                {[0, 1, 2].map((dot) => (
                  <span
                    key={dot}
                    className="w-1.5 h-1.5 rounded-full bg-gray-400 animate-bounce"
                    style={{ animationDelay: `${dot * 0.18}s` }}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
);

export default HowItWorksSection;
