import { motion } from "framer-motion";
import { useTypewriter } from "../hooks/useTypewriter";

const MESSAGES = [
  "Track expenses intelligently with WalletIQ",
  "Your personal AI-powered finance assistant",
  "RAG-based insights from your spending habits",
  "Understand where your money really goes",
];

const container = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08 } },
};

const item = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4 } },
};

const LoginBrandingPanel: React.FC = () => {
  const text = useTypewriter(MESSAGES);

  return (
    <div className="hidden lg:flex w-1/2 relative overflow-hidden bg-gradient-to-br from-red-700 via-purple-900 to-black text-white">
      {/* Overlay blur */}
      <div className="absolute inset-0 bg-black/20 backdrop-blur-2xl" />

      {/* Dot grid */}
      <div
        className="absolute inset-0 opacity-10"
        style={{
          backgroundImage:
            "radial-gradient(circle at 1px 1px, white 1px, transparent 0)",
          backgroundSize: "40px 40px",
        }}
      />

      {/* Floating glow blob */}
      <motion.div
        animate={{
          x: [0, 30, -20, 0],
          y: [0, -20, 20, 0],
          scale: [1, 1.1, 0.95, 1],
        }}
        transition={{ duration: 10, repeat: Infinity }}
        className="absolute left-20 top-1/3 w-40 h-40 bg-red-500/30 blur-3xl"
      />

      {/* Content */}
      <motion.div
        initial="hidden"
        animate="show"
        variants={container}
        className="relative z-10 flex flex-col justify-center px-16 h-full"
      >
        <motion.h1 variants={item} className="text-4xl font-semibold mb-6">
          Wallet<span className="text-red-400">IQ</span>
        </motion.h1>

        <motion.div variants={item} className="h-20">
          <p className="text-2xl">
            {text}
            <span className="animate-pulse">|</span>
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default LoginBrandingPanel;
