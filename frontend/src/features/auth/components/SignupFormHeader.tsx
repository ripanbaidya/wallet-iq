import { motion, type Variants } from "framer-motion";

interface Props {
  variants: Variants;
  success: string;
}

const SignupFormHeader: React.FC<Props> = ({ variants, success }) => (
  <>
    <motion.div variants={variants} className="mb-8">
      <h2 className="text-3xl font-semibold tracking-tight">
        Create your account
      </h2>
      <p className="text-sm text-gray-500 mt-2">
        Get started with your AI-powered finance system.
      </p>
    </motion.div>

    {success && (
      <motion.div
        initial={{ opacity: 0, y: -8 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700"
      >
        {success}
      </motion.div>
    )}
  </>
);

export default SignupFormHeader;
