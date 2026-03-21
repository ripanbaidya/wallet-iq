import React from "react";
import { motion, type Variants } from "framer-motion";
import { RiShieldCheckLine } from "react-icons/ri";

interface Props {
  variants: Variants;
}

const LoginFormHeader: React.FC<Props> = ({ variants }) => (
  <motion.div variants={variants} className="mb-8 text-center">
    <div className="flex justify-center mb-3 text-indigo-500 text-2xl">
      <RiShieldCheckLine />
    </div>
    <h2 className="text-3xl font-semibold text-gray-900">Welcome back</h2>
    <p className="mt-2 text-sm text-gray-500">Securely login to your account</p>
  </motion.div>
);

export default LoginFormHeader;
