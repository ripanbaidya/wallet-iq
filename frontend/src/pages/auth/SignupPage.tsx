import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authService } from "../../services/authService";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { motion } from "framer-motion";

export default function SignupPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    fullName: "",
    password: "",
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [agreeToTerms, setAgreeToTerms] = useState(false);

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.fullName) e.fullName = "Full name is required";
    if (!form.email) e.email = "Email is required";
    if (!form.password) e.password = "Password is required";
    if (!agreeToTerms) e.terms = "Accept terms to continue";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });

    if (errors[e.target.name]) {
      setErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    setErrors({});
    setSuccess("");

    try {
      await authService.signup(form);

      setSuccess("Account created successfully 🎉");

      setTimeout(() => {
        navigate("/login");
      }, 1500);
    } catch (err: any) {
      const backendErrors = err?.response?.data?.errors;

      if (backendErrors) {
        setErrors(backendErrors);
      } else {
        setErrors({
          submit:
            err?.response?.data?.message || "Signup failed. Please try again.",
        });
      }
    } finally {
      setLoading(false);
    }
  };

  // Animations
  const container = {
    hidden: {},
    show: {
      transition: {
        staggerChildren: 0.08,
      },
    },
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
      {/* Left Panel */}
      <div className="flex w-full lg:w-1/2 items-center justify-center bg-gray-50 px-6">
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="w-full max-w-md"
        >
          {/* Header */}
          <motion.div variants={item} className="mb-10">
            <h2 className="text-3xl font-semibold tracking-tight leading-tight">
              Create your account
            </h2>

            <p className="text-sm text-gray-500 mt-3">
              Get started with your AI-powered finance system.
            </p>
          </motion.div>

          {/* Success Message */}
          {success && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-4 text-green-600 text-sm font-medium"
            >
              {success}
            </motion.div>
          )}

          {/* Error */}
          {errors.submit && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="mb-4 text-red-500 text-sm"
            >
              {errors.submit}
            </motion.div>
          )}

          <motion.form
            variants={container}
            onSubmit={handleSubmit}
            className="space-y-3"
          >
            {/* Full Name */}
            <motion.div variants={item}>
              <label className="text-sm text-gray-600">Full name</label>
              <input
                type="text"
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                className="w-full mt-1 px-4 py-3 border rounded-lg focus:ring-2 focus:ring-black outline-none"
              />
              <p className="text-xs text-red-400 mt-1">{errors.fullName}</p>
            </motion.div>

            {/* Email */}
            <motion.div variants={item}>
              <label className="text-sm text-gray-600">Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                className="w-full mt-1 px-4 py-3 border rounded-lg focus:ring-2 focus:ring-black outline-none"
              />
              <p className="text-xs text-red-400 mt-1">{errors.email}</p>
            </motion.div>

            {/* Password */}
            <motion.div variants={item}>
              <label className="text-sm text-gray-600">Password</label>

              <div className="relative mt-1">
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  className="w-full px-4 py-3 pr-12 border rounded-lg focus:ring-2 focus:ring-black outline-none"
                />

                <motion.button
                  whileTap={{ scale: 0.9 }}
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-800 transition"
                >
                  {showPassword ? (
                    <IoEyeOffOutline className="w-5 h-5" />
                  ) : (
                    <IoEyeOutline className="w-5 h-5" />
                  )}
                </motion.button>
              </div>

              <p className="text-xs text-red-400 mt-1">{errors.password}</p>
            </motion.div>

            {/* Terms */}
            <motion.div variants={item}>
              <label className="flex items-center gap-2 text-sm text-gray-600">
                <input
                  type="checkbox"
                  checked={agreeToTerms}
                  onChange={(e) => setAgreeToTerms(e.target.checked)}
                />
                I agree to Terms & Privacy Policy
              </label>
              <p className="text-xs text-red-400 mt-1">{errors.terms}</p>
            </motion.div>

            {/* Submit */}
            <motion.button
              variants={item}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.97 }}
              type="submit"
              disabled={loading}
              className="w-full bg-black text-white py-3 rounded-full hover:bg-gray-800 transition flex items-center justify-center"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Creating...
                </span>
              ) : (
                "Create account"
              )}
            </motion.button>

            {/* Divider */}
            <motion.div variants={item} className="flex items-center my-6">
              <div className="flex-1 h-px bg-gray-300" />
              <span className="px-3 text-sm text-gray-500">OR</span>
              <div className="flex-1 h-px bg-gray-300" />
            </motion.div>

            {/* Social */}
            <motion.div variants={item} className="mb-6 text-center">
              <p className="text-xs text-gray-400 mb-4 tracking-wider">
                Or continue with
              </p>

              <div className="flex justify-center gap-3">
                <motion.button
                  whileHover={{ scale: 1.1 }}
                  className="w-12 h-12 rounded-full bg-white shadow-md flex items-center justify-center"
                >
                  <img
                    src="https://www.svgrepo.com/show/475656/google-color.svg"
                    className="w-5 h-5"
                  />
                </motion.button>

                <motion.button
                  whileHover={{ scale: 1.1 }}
                  className="w-12 h-12 rounded-full bg-white shadow-md flex items-center justify-center"
                >
                  <img
                    src="https://www.svgrepo.com/show/512317/github-142.svg"
                    className="w-5 h-5"
                  />
                </motion.button>
              </div>
            </motion.div>
          </motion.form>

          {/* Footer */}
          <motion.p
            variants={item}
            className="text-sm text-gray-500 text-center mt-6"
          >
            Already have an account?{" "}
            <Link
              to="/login"
              className="font-medium text-black hover:text-blue-700 transition"
            >
              Login
            </Link>
          </motion.p>
        </motion.div>
      </div>

      {/* Right Panel (unchanged) */}
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
            className="space-y-8 font-[Space_Grotesk]"
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
