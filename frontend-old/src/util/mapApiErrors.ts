import type { ErrorResponse } from "../types/api.types";

export function mapApiErrors(errorResponse: ErrorResponse) {
  const error = errorResponse?.error;

  if (!error) {
    return { submit: "Something went wrong" };
  }

  // Field validation errors
  if (error.errors && Array.isArray(error.errors)) {
    const fieldErrors: Record<string, string> = {};

    error.errors.forEach((err: any) => {
      if (!fieldErrors[err.field]) {
        fieldErrors[err.field] = err.message;
      }
    });

    return fieldErrors;
  }

  // Business logic errors (e.g. email exists)
  if (error.code === "EMAIL_ALREADY_EXISTS") {
    return {
      email: error.message,
    };
  }

  // fallback
  return {
    submit: error.message || "Something went wrong",
  };
}