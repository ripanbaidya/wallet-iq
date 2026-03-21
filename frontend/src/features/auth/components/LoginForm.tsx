import { Link } from "react-router-dom";
import { useState } from "react";
import { motion, type Variants } from "framer-motion";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { FormError } from "../../../shared/components/ui/FormError";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { ROUTES } from "../../../routes/routePaths";

interface Props {
  email: string;
  password: string;
  fieldErrors: Record<string, string>;
  formError: string | null;
  isPending: boolean;
  onEmailChange: (val: string) => void;
  onPasswordChange: (val: string) => void;
  onSubmit: (e: React.FormEvent) => void;
  containerVariants: Variants;
  itemVariants: Variants;
}

const LoginForm: React.FC<Props> = ({
  email,
  password,
  fieldErrors,
  formError,
  isPending,
  onEmailChange,
  onPasswordChange,
  onSubmit,
  containerVariants,
  itemVariants,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <motion.div
      variants={containerVariants}
      initial="hidden"
      animate="show"
      className="w-full max-w-md"
    >
      {/* Top-level form error */}
      <motion.div variants={itemVariants}>
        <FormError error={formError} />
      </motion.div>

      <motion.form
        onSubmit={onSubmit}
        variants={containerVariants}
        className="space-y-5 mt-4"
      >
        {/* Email */}
        <motion.div variants={itemVariants}>
          <label className="block text-sm text-gray-600 mb-1">Email</label>
          <input
            type="email"
            value={email}
            placeholder="ripanbaidya@example.com"
            onChange={(e) => onEmailChange(e.target.value)}
            className="w-full px-4 py-3 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
          />
          <FieldErrorMessage message={fieldErrors.email} />
        </motion.div>

        {/* Password */}
        <motion.div variants={itemVariants}>
          <label className="block text-sm text-gray-600 mb-1">Password</label>
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              placeholder="********"
              onChange={(e) => onPasswordChange(e.target.value)}
              className="w-full px-4 py-3 pr-12 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
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
          variants={itemVariants}
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
        variants={itemVariants}
        className="text-sm text-center mt-6 text-gray-500"
      >
        Don't have an account?{" "}
        <Link
          to={ROUTES.signup}
          className="font-medium text-black hover:text-gray-600 transition"
        >
          Register
        </Link>
      </motion.p>
    </motion.div>
  );
};

export default LoginForm;
