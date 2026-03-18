import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { motion } from "framer-motion";

import { useAppMutation } from "../../hooks/useAppMutation";
import { authService } from "../../services/authService";
import { AppError } from "../../errors/AppError";
import { FormError } from "../../components/ui/FormError";
import { FieldErrorMessage } from "../../components/ui/FieldErrorMessage";

export default function SignupPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [success, setSuccess] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [agreeToTerms, setAgreeToTerms] = useState(false);

  // ✅ useAppMutation — consistent with LoginPage, errors normalized via AppError
  const { mutate: signup, isPending } = useAppMutation({
    mutationFn: () => authService.signup(form),

    onSuccess: () => {
      setSuccess("Account created successfully 🎉");
      setTimeout(() => navigate("/login"), 1500);
    },

    onError: (error: AppError) => {
      setFieldErrors({});
      setFormError(null);
      if (error.isValidation) {
        setFieldErrors(error.toFieldErrorMap());
      } else if (error.isConflict) {
        // e.g. EMAIL_ALREADY_EXISTS — show on the email field directly
        setFieldErrors({ email: error.message });
      } else {
        setFormError(error.message);
      }
    },
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    // Clear field error on change
    if (fieldErrors[e.target.name]) {
      setFieldErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Client-side guard for terms checkbox (not a backend field)
    if (!agreeToTerms) {
      setFieldErrors({ terms: "You must accept the terms to continue" });
      return;
    }

    setFieldErrors({});
    setFormError(null);
    signup(undefined);
  };

  const container = {
    hidden: {},
    show: { transition: { staggerChildren: 0.08 } },
  };

  const item = {
    hidden: { opacity: 0, y: 15 },
    show: { opacity: 1, y: 0, transition: { duration: 0.4 } },
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="min-h-screen flex font-sans"
    >
      {/* LEFT — form panel */}
      <div className="flex w-full lg:w-1/2 items-center justify-center bg-gray-50 px-6">
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="w-full max-w-md"
        >
          {/* Header */}
          <motion.div variants={item} className="mb-8">
            <h2 className="text-3xl font-semibold tracking-tight">
              Create your account
            </h2>
            <p className="text-sm text-gray-500 mt-2">
              Get started with your AI-powered finance system.
            </p>
          </motion.div>

          {/* Success banner */}
          {success && (
            <motion.div
              initial={{ opacity: 0, y: -8 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700"
            >
              {success}
            </motion.div>
          )}

          {/* Top-level form error */}
          <motion.div variants={item}>
            <FormError error={formError} />
          </motion.div>

          <motion.form
            variants={container}
            onSubmit={handleSubmit}
            className="space-y-4 mt-4"
          >
            {/* Full Name */}
            <motion.div variants={item}>
              <label className="block text-sm text-gray-600 mb-1">
                Full name
              </label>
              <input
                type="text"
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
              />
              <FieldErrorMessage message={fieldErrors.fullName} />
            </motion.div>

            {/* Email */}
            <motion.div variants={item}>
              <label className="block text-sm text-gray-600 mb-1">
                Email
              </label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
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
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  className="w-full px-4 py-3 pr-12 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-700 transition"
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

            {/* Terms */}
            <motion.div variants={item}>
              <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
                <input
                  type="checkbox"
                  checked={agreeToTerms}
                  onChange={(e) => {
                    setAgreeToTerms(e.target.checked);
                    if (fieldErrors.terms) {
                      setFieldErrors((prev) => ({ ...prev, terms: "" }));
                    }
                  }}
                  className="rounded"
                />
                I agree to the Terms &amp; Privacy Policy
              </label>
              <FieldErrorMessage message={fieldErrors.terms} />
            </motion.div>

            {/* Submit */}
            <motion.button
              variants={item}
              type="submit"
              disabled={isPending}                             // ✅ no manual loading state
              className="w-full bg-black text-white py-3 rounded-full hover:bg-gray-800 transition disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {isPending ? (
                <>
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Creating...
                </>
              ) : (
                "Create account"
              )}
            </motion.button>
          </motion.form>

          <motion.p
            variants={item}
            className="text-sm text-gray-500 text-center mt-6"
          >
            Already have an account?{" "}
            <Link
              to="/login"
              className="font-medium text-black hover:text-gray-600 transition"
            >
              Login
            </Link>
          </motion.p>
        </motion.div>
      </div>

      {/* RIGHT — branding panel */}
      <div className="hidden lg:flex w-1/2 relative overflow-hidden bg-[#020617] text-white">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 0.5, scale: 1 }}
          transition={{ duration: 2 }}
          className="absolute -inset-[10%]"
          style={{
            background: `radial-gradient(circle at 20% 30%, #059669 0%, transparent 50%), 
                         radial-gradient(circle at 80% 70%, #4f46e5 0%, transparent 50%),
                         radial-gradient(circle at 50% 10%, #0ea5e9 0%, transparent 50%)`,
            filter: "blur(80px)",
          }}
        />
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
              Leveraging RAG systems to turn your transaction data into
              actionable wealth strategies.
            </p>
          </motion.div>
        </div>
      </div>
    </motion.div>
  );
}