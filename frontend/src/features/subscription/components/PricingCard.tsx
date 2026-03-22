import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../../routes/routePaths";

interface Props {
  isActive: boolean;
  isCreatingOrder: boolean;
  isProcessing: boolean;
  isLoaded: boolean;
  onSubscribe: () => void;
}

const FEATURES = [
  "Unlimited AI financial Q&A",
  "RAG-powered answers from your transactions",
  "Budget & savings goal insights",
  "Spending pattern analysis",
  "Natural language queries",
];

const PricingCard: React.FC<Props> = ({
  isActive,
  isCreatingOrder,
  isProcessing,
  isLoaded,
  onSubscribe,
}) => {
  const navigate = useNavigate();

  return (
    <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
      {/* Dark header */}
      <div className="bg-[#0f0f0f] px-6 py-8 text-center relative overflow-hidden">
        <div
          className="absolute inset-0 opacity-60 pointer-events-none"
          style={{
            backgroundImage:
              "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
            backgroundSize: "20px 20px",
          }}
        />
        <div className="relative">
          <p className="text-xs text-white/40 uppercase tracking-widest mb-3">
            Monthly Plan
          </p>
          <div className="flex items-end justify-center gap-1">
            <span className="text-white text-2xl font-medium">₹</span>
            <span className="text-white text-5xl font-black">199</span>
            <span className="text-white/40 text-sm mb-2">/ month</span>
          </div>
          <p className="text-white/40 text-xs mt-3">
            30 days of unlimited AI chat access
          </p>
        </div>
      </div>

      {/* Features list */}
      <div className="px-6 py-5 space-y-3">
        {FEATURES.map((feature) => (
          <div key={feature} className="flex items-center gap-3">
            <span className="w-5 h-5 rounded-full bg-gray-900 flex items-center justify-center shrink-0">
              <svg
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#e8ff4f"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </span>
            <span className="text-sm text-gray-700">{feature}</span>
          </div>
        ))}
      </div>

      {/* CTA */}
      <div className="px-6 pb-6">
        {isActive ? (
          <button
            onClick={() => navigate(ROUTES.chat)}
            className="w-full py-3 bg-gray-900 text-white text-sm font-semibold rounded-xl hover:bg-black transition-colors"
          >
            Go to Chat →
          </button>
        ) : (
          <button
            onClick={onSubscribe}
            disabled={isCreatingOrder || isProcessing || !isLoaded}
            className="w-full py-3 bg-gray-900 text-white text-sm font-semibold rounded-xl hover:bg-black disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
          >
            {isCreatingOrder || isProcessing ? (
              <>
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                {isCreatingOrder ? "Preparing payment..." : "Verifying..."}
              </>
            ) : (
              "Subscribe for ₹199"
            )}
          </button>
        )}

        <p className="text-xs text-gray-400 text-center mt-3">
          Secured by Razorpay · No auto-renewal
        </p>
      </div>
    </div>
  );
};

export default PricingCard;
