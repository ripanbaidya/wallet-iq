import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { authService } from "../authService";
import { AppError } from "../../../api/errorParser";
import { ROUTES } from "../../../routes/routePaths";

interface SignupForm {
  fullName: string;
  email: string;
  password: string;
}

export function useSignup() {
  const navigate = useNavigate();

  const [form, setForm] = useState<SignupForm>({
    fullName: "",
    email: "",
    password: "",
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [success, setSuccess] = useState("");
  const [redirectCountdown, setRedirectCountdown] = useState<number | null>(null);
  const [agreeToTerms, setAgreeToTerms] = useState(false);

  const { mutate: signup, isPending } = useAppMutation({
    mutationFn: () => authService.signup(form),

    onSuccess: () => {
      setSuccess("Account created successfully 🎉");
      setRedirectCountdown(3);
    },

    onError: (error: AppError) => {
      setFieldErrors({});
      setFormError(null);
      if (error.isValidation) {
        setFieldErrors(error.toFieldErrorMap());
      } else if (error.isConflict) {
        setFieldErrors({ email: error.message });
      } else {
        setFormError(error.message);
      }
    },
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    if (fieldErrors[e.target.name]) {
      setFieldErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    }
  };

  const handleTermsChange = (checked: boolean) => {
    setAgreeToTerms(checked);
    if (fieldErrors.terms) {
      setFieldErrors((prev) => ({ ...prev, terms: "" }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!agreeToTerms) {
      setFieldErrors({ terms: "You must accept the terms to continue" });
      return;
    }
    setFieldErrors({});
    setFormError(null);
    signup(undefined);
  };

  useEffect(() => {
    if (!success || redirectCountdown === null) return;
    if (redirectCountdown === 0) {
      navigate(ROUTES.login);
      return;
    }

    const timer = window.setTimeout(() => {
      setRedirectCountdown((prev) => (prev === null ? prev : prev - 1));
    }, 1000);

    return () => window.clearTimeout(timer);
  }, [success, redirectCountdown, navigate]);

  return {
    form,
    fieldErrors,
    formError,
    success,
    redirectCountdown,
    agreeToTerms,
    isPending,
    handleChange,
    handleTermsChange,
    handleSubmit,
  };
}
