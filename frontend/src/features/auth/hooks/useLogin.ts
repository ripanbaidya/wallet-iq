import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { authService } from "../authService";
import { adminService } from "../../admin/adminService";
import { useAuthStore } from "../../../store/authStore";
import { AppError } from "../../../api/errorParser";
import { ROUTES } from "../../../routes/routePaths";

export function useLogin() {
  const navigate = useNavigate();
  const { setAuth, setIsAdmin } = useAuthStore();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  const { mutate, isPending } = useAppMutation({
    mutationFn: () => authService.login({ email, password }),

    onSuccess: async (res) => {
      setAuth(
        res.data.user,
        res.data.tokens.accessToken,
        res.data.tokens.refreshToken,
      );

      // Probe admin endpoint once — 200 → admin, 403 → regular user
      try {
        await adminService.getUserCount("USER", true);
        setIsAdmin(true);
      } catch {
        setIsAdmin(false);
      }

      navigate(ROUTES.dashboard);
    },

    onError: (error: AppError) => {
      setFieldErrors({});
      setFormError(null);
      if (error.isValidation) {
        setFieldErrors(error.toFieldErrorMap());
      } else {
        setFormError(error.message);
      }
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutate(undefined);
  };

  return {
    email,
    setEmail,
    password,
    setPassword,
    fieldErrors,
    formError,
    isPending,
    handleSubmit,
  };
}