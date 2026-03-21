import { motion } from "framer-motion";

const SignupBrandingPanel: React.FC = () => (
  <div className="hidden lg:flex w-1/2 relative overflow-hidden bg-[#020617] text-white">
    {/* Gradient glow blobs */}
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 0.5, scale: 1 }}
      transition={{ duration: 2 }}
      className="absolute -inset-[10%]"
      style={{
        background: `
          radial-gradient(circle at 20% 30%, #059669 0%, transparent 50%),
          radial-gradient(circle at 80% 70%, #4f46e5 0%, transparent 50%),
          radial-gradient(circle at 50% 10%, #0ea5e9 0%, transparent 50%)
        `,
        filter: "blur(80px)",
      }}
    />

    {/* Content */}
    <div className="relative z-10 flex flex-col justify-center px-20 h-full">
      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="space-y-8"
      >
        <h1 className="text-5xl font-bold tracking-tighter">
          Wallet
          <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            IQ
          </span>
        </h1>

        <p className="text-3xl font-light leading-tight max-w-sm">
          Master your money with{" "}
          <span className="font-semibold italic text-emerald-400">
            precision.
          </span>
        </p>

        <p className="text-base text-slate-400 max-w-xs">
          Leveraging RAG systems to turn your transaction data into actionable
          wealth strategies.
        </p>
      </motion.div>
    </div>
  </div>
);

export default SignupBrandingPanel;
