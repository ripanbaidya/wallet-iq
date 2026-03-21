import { motion } from "framer-motion";
import { useSignup } from "../hooks/useSignup";
import SignupBrandingPanel from "../components/SignupBrandingPanel";
import SignupFormHeader from "../components/SignupFormHeader";
import SignupForm from "../components/SignupForm";

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
      className="min-h-screen flex font-sans"
    >
      {/* Left — form panel */}
      <div className="flex w-full lg:w-1/2 items-center justify-center bg-gray-50 px-6">
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

      <SignupBrandingPanel />
    </motion.div>
  );
}
