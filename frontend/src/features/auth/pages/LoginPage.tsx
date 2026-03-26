import { motion, type Variants } from "framer-motion";
import { Link } from "react-router-dom";
import { useLogin } from "../hooks/useLogin";
import LoginBrandingPanel from "../components/LoginBrandingPanel";
import LoginFormHeader from "../components/LoginFormHeader";
import LoginForm from "../components/LoginForm";
import { ROUTES } from "../../../routes/routePaths";

const containerVariants: Variants = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08 } },
};

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4 } },
};

export default function LoginPage() {
  const {
    email,
    setEmail,
    password,
    setPassword,
    fieldErrors,
    formError,
    isPending,
    handleSubmit,
  } = useLogin();

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="min-h-screen flex flex-col font-sans"
    >
      {/* Mobile-only top bar — shown only when the branding panel is hidden (< lg) */}
      <div className="lg:hidden flex items-center px-6 h-14 bg-white border-b border-gray-100 shrink-0">
        <Link
          to={ROUTES.home}
          className="text-xl font-bold tracking-tight text-gray-900 select-none"
        >
          Wallet
          <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            IQ
          </span>
        </Link>
      </div>

      {/* Main row */}
      <div className="flex flex-1 min-h-0">
        {/* Left — branding panel (desktop only) */}
        <LoginBrandingPanel />

        {/* Right — form panel */}
        <div className="flex w-full lg:w-1/2 flex-col bg-gray-50">
          {/* Desktop logo — sits above the form, hidden on mobile */}
          <div className="hidden lg:flex items-center px-8 pt-6">
            <Link
              to={ROUTES.home}
              className="text-xl font-bold tracking-tight text-gray-900 select-none hover:opacity-75 transition-opacity"
            >
              Wallet
              <span className="text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
                IQ
              </span>
            </Link>
          </div>

          {/* Vertically-centered form */}
          <div className="flex flex-1 items-center justify-center px-6 py-8">
            <div className="w-full max-w-md">
              <LoginFormHeader variants={itemVariants} />

              <LoginForm
                email={email}
                password={password}
                fieldErrors={fieldErrors}
                formError={formError}
                isPending={isPending}
                onEmailChange={setEmail}
                onPasswordChange={setPassword}
                onSubmit={handleSubmit}
                containerVariants={containerVariants}
                itemVariants={itemVariants}
              />
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );
}
