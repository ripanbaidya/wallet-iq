import { useState } from "react";
import { Link } from "react-router-dom";
import { motion, type Variants } from "framer-motion";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { FormError } from "../../../shared/components/ui/FormError";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { ROUTES } from "../../../routes/routePaths";

interface Props {
  form: { fullName: string; email: string; password: string };
  fieldErrors: Record<string, string>;
  formError: string | null;
  agreeToTerms: boolean;
  isPending: boolean;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onTermsChange: (checked: boolean) => void;
  onSubmit: (e: React.FormEvent) => void;
  containerVariants: Variants;
  itemVariants: Variants;
}

const SignupForm: React.FC<Props> = ({
  form,
  fieldErrors,
  formError,
  agreeToTerms,
  isPending,
  onChange,
  onTermsChange,
  onSubmit,
  containerVariants,
  itemVariants,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <>
      {/* Top-level form error */}
      <motion.div variants={itemVariants}>
        <FormError error={formError} />
      </motion.div>

      <motion.form
        variants={containerVariants}
        onSubmit={onSubmit}
        className="space-y-4 mt-4"
      >
        {/* Full Name */}
        <motion.div variants={itemVariants}>
          <label className="block text-sm text-gray-600 mb-1">Full name</label>
          <input
            type="text"
            name="fullName"
            value={form.fullName}
            placeholder="Ripan Baidya"
            onChange={onChange}
            className="w-full px-4 py-3 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
          />
          <FieldErrorMessage message={fieldErrors.fullName} />
        </motion.div>

        {/* Email */}
        <motion.div variants={itemVariants}>
          <label className="block text-sm text-gray-600 mb-1">Email</label>
          <input
            type="email"
            name="email"
            value={form.email}
            placeholder="ripanbaidya@example.com"
            onChange={onChange}
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
              name="password"
              value={form.password}
              placeholder="********"
              onChange={onChange}
              className="w-full px-4 py-3 pr-12 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-black bg-white"
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
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
        <motion.div variants={itemVariants}>
          <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
            <input
              type="checkbox"
              checked={agreeToTerms}
              onChange={(e) => onTermsChange(e.target.checked)}
              className="rounded"
            />
            I agree to the Terms &amp; Privacy Policy
          </label>
          <FieldErrorMessage message={fieldErrors.terms} />
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
              Creating...
            </>
          ) : (
            "Create account"
          )}
        </motion.button>
      </motion.form>

      <motion.p
        variants={itemVariants}
        className="text-sm text-gray-500 text-center mt-6"
      >
        Already have an account?{" "}
        <Link
          to={ROUTES.login}
          className="font-medium text-black hover:text-gray-500 transition"
        >
          Login
        </Link>
      </motion.p>
    </>
  );
};

export default SignupForm;
