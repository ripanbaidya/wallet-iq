import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { motion } from "framer-motion";

import { useAppMutation } from "../../hooks/useAppMutation";
import { authService } from "../../services/authService";
import { adminService } from "../../services/adminService";
import { useAuthStore } from "../../store/authStore";
import { AppError } from "../../errors/AppError";
import { FormError } from "../../components/ui/FormError";
import { FieldErrorMessage } from "../../components/ui/FieldErrorMessage";

const messages = [
  "Track expenses intelligently with WalletIQ",
  "Your personal AI-powered finance assistant",
  "RAG-based insights from your spending habits",
  "Understand where your money really goes",
];

export default function LoginPage() {
  const navigate = useNavigate();
  const { setAuth, setIsAdmin } = useAuthStore();

  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  // typing animation
  const [text, setText] = useState("");
  const [index, setIndex] = useState(0);
  const [subIndex, setSubIndex] = useState(0);
  const [deleting, setDeleting] = useState(false);

  const { mutate, isPending } = useAppMutation({
    mutationFn: () => authService.login({ email, password }),

    onSuccess: async (res) => {
      // Store auth immediately so the Axios interceptor can
      // attach the Bearer token for the admin probe below.
      setAuth(
        res.data.user,
        res.data.tokens.accessToken,
        res.data.tokens.refreshToken,
      );

      // Probe the admin endpoint once at login time.
      // 200 → admin, 403 → regular user.
      try {
        await adminService.getUserCount("USER", true);
        setIsAdmin(true);
      } catch {
        setIsAdmin(false);
      }

      navigate("/dashboard");
    },

    onError: (error: AppError) => {
      setFieldErrors({});
      setFormError(null);
      if (error.isValidation) {
        setFieldErrors(error.toFieldErrorMap());
      } else {
        setFormError(error.message);
      }
    },
  });

  useEffect(() => {
    const currentMessage = messages[index];
    if (!currentMessage) return;

    if (!deleting && subIndex === currentMessage.length) {
      setTimeout(() => setDeleting(true), 1200);
      return;
    }
    if (deleting && subIndex === 0) {
      setDeleting(false);
      setIndex((prev) => (prev + 1) % messages.length);
      return;
    }

    const timeout = setTimeout(
      () => {
        setSubIndex((prev) => prev + (deleting ? -1 : 1));
        setText(currentMessage.substring(0, subIndex));
      },
      deleting ? 30 : 50,
    );

    return () => clearTimeout(timeout);
  }, [subIndex, index, deleting]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutate(undefined);
  };

  const container = {
    hidden: {},
    show: { transition: { staggerChildren: 0.08 } },
  };

  const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0, transition: { duration: 0.4 } },
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="min-h-screen flex font-sans"
    >
      {/* LEFT — branding panel */}
      <div className="hidden lg:flex w-1/2 relative overflow-hidden bg-gradient-to-br from-red-700 via-purple-900 to-black text-white">
        <div className="absolute inset-0 bg-black/20 backdrop-blur-2xl" />
        <div
          className="absolute inset-0 opacity-10"
          style={{
            backgroundImage:
              "radial-gradient(circle at 1px 1px, white 1px, transparent 0)",
            backgroundSize: "40px 40px",
          }}
        />
        <motion.div
          animate={{
            x: [0, 30, -20, 0],
            y: [0, -20, 20, 0],
            scale: [1, 1.1, 0.95, 1],
          }}
          transition={{ duration: 10, repeat: Infinity }}
          className="absolute left-20 top-1/3 w-40 h-40 bg-red-500/30 blur-3xl"
        />
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

      {/* RIGHT — form panel */}
      <div className="flex w-full lg:w-1/2 items-center justify-center bg-gray-50 px-6">
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="w-full max-w-md"
        >
          <motion.div variants={item} className="mb-6">
            <h2 className="text-3xl font-semibold">Welcome back</h2>
          </motion.div>

          {/* Top-level form error */}
          <motion.div variants={item}>
            <FormError error={formError} />
          </motion.div>

          <motion.form
            onSubmit={handleSubmit}
            variants={container}
            className="space-y-5 mt-4"
          >
            {/* Email */}
            <motion.div variants={item}>
              <label className="block text-sm text-gray-600 mb-1">
                Email address
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
              />
              <FieldErrorMessage message={fieldErrors.email} />
            </motion.div>

            {/* Password */}
            <motion.div variants={item}>
              <label className="block text-sm text-gray-600 mb-1">
                Password
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-3 pr-12 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-700"
                >
                  {showPassword ? (
                    <IoEyeOffOutline className="w-5 h-5" />
                  ) : (
                    <IoEyeOutline className="w-5 h-5" />
                  )}
                </button>
              </div>
              <FieldErrorMessage message={fieldErrors.password} />
            </motion.div>

            {/* Submit */}
            <motion.button
              variants={item}
              type="submit"
              disabled={isPending}
              className="w-full bg-black text-white py-3 rounded-full hover:bg-gray-800 transition disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {isPending ? (
                <>
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Signing in...
                </>
              ) : (
                "Login"
              )}
            </motion.button>
          </motion.form>

          <motion.p
            variants={item}
            className="text-sm text-center mt-6 text-gray-500"
          >
            Don't have an account?{" "}
            <Link
              to="/signup"
              className="font-medium text-black hover:text-gray-600 transition"
            >
              Register
            </Link>
          </motion.p>
        </motion.div>
      </div>
    </motion.div>
  );
}
