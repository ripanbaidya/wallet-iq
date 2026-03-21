import { motion, type Variants } from "framer-motion";
import { useLogin } from "../hooks/useLogin";
import LoginBrandingPanel from "../components/LoginBrandingPanel";
import LoginFormHeader from "../components/LoginFormHeader";
import LoginForm from "../components/LoginForm";

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
      className="min-h-screen flex font-sans"
    >
      <LoginBrandingPanel />

      {/* Right — form panel */}
      <div className="flex w-full lg:w-1/2 items-center justify-center bg-gray-50 px-6">
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
    </motion.div>
  );
}
