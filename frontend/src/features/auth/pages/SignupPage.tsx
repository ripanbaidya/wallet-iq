import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { useSignup } from "../hooks/useSignup";
import SignupBrandingPanel from "../components/SignupBrandingPanel";
import SignupFormHeader from "../components/SignupFormHeader";
import SignupForm from "../components/SignupForm";
import { ROUTES } from "../../../routes/routePaths";

const containerVariants = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08 } },
};

const itemVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4 } },
};

export default function SignupPage() {
  const {
    form,
    fieldErrors,
    formError,
    success,
    agreeToTerms,
    isPending,
    handleChange,
    handleTermsChange,
    handleSubmit,
  } = useSignup();

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="min-h-screen flex flex-col font-sans"
    >
      {/* Mobile-only top bar — shown only when the branding panel is hidden (< lg) */}
      <div className="lg:hidden flex items-center px-6 h-14 bg-gray-50 border-b border-gray-100 shrink-0">
        <Link
          to={ROUTES.home}
          className="text-xl font-bold tracking-tight text-gray-900 select-none"
        >
          Wallet
          <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            IQ
          </span>
        </Link>
      </div>

      {/* Main row */}
      <div className="flex flex-1 min-h-0">
        {/* Left — form panel */}
        <div className="flex w-full lg:w-1/2 flex-col items-center bg-gray-50">
          {/* Desktop logo — hidden on mobile */}
          <div className="hidden lg:flex w-full items-center px-8 pt-6">
            <Link
              to={ROUTES.home}
              className="text-xl font-bold tracking-tight text-gray-900 select-none hover:opacity-75 transition-opacity"
            >
              Wallet
              <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
                IQ
              </span>
            </Link>
          </div>

          {/* Vertically-centered form */}
          <div className="flex flex-1 items-center justify-center w-full px-6 py-8">
            <motion.div
              variants={containerVariants}
              initial="hidden"
              animate="show"
              className="w-full max-w-md"
            >
              <SignupFormHeader variants={itemVariants} success={success} />

              <SignupForm
                form={form}
                fieldErrors={fieldErrors}
                formError={formError}
                agreeToTerms={agreeToTerms}
                isPending={isPending}
                onChange={handleChange}
                onTermsChange={handleTermsChange}
                onSubmit={handleSubmit}
                containerVariants={containerVariants}
                itemVariants={itemVariants}
              />
            </motion.div>
          </div>
        </div>

        {/* Right — branding panel (desktop only) */}
        <SignupBrandingPanel />
      </div>
    </motion.div>
  );
}
