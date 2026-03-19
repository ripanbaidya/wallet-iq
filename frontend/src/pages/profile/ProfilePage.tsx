import { useState, useEffect, useRef } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { userService } from "../../services/userService";
import { authService } from "../../services/authService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";
import { FieldErrorMessage } from "../../components/ui/FieldErrorMessage";
import { FormError } from "../../components/ui/FormError";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";
import { useAuthStore } from "../../store/authStore";

// ── Constants ─────────────────────────────────────────────────────────────────

const OTP_EXPIRY_SECONDS = 5 * 60; // matches backend: app.otp.expiry-minutes: 5

// ── Helpers ───────────────────────────────────────────────────────────────────

const getInitials = (name: string) =>
  name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join("");

const formatCountdown = (seconds: number) => {
  const m = Math.floor(seconds / 60)
    .toString()
    .padStart(2, "0");
  const s = (seconds % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
};

// ── Info row ──────────────────────────────────────────────────────────────────

const InfoRow: React.FC<{ label: string; value: React.ReactNode }> = ({
  label,
  value,
}) => (
  <div className="flex items-center justify-between py-3.5 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500">{label}</span>
    <span className="text-sm text-gray-900 font-medium text-right">
      {value}
    </span>
  </div>
);

// ── Email Verification component ──────────────────────────────────────────────

type VerifyStep = "idle" | "otp";

interface EmailVerificationProps {
  email: string;
  onVerified: () => void;
}

function EmailVerification({ email, onVerified }: EmailVerificationProps) {
  const [step, setStep] = useState<VerifyStep>("idle");
  const [otp, setOtp] = useState("");
  const [countdown, setCountdown] = useState(0);
  const [otpError, setOtpError] = useState("");
  const [done, setDone] = useState(false);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, []);

  const startCountdown = () => {
    setCountdown(OTP_EXPIRY_SECONDS);
    if (timerRef.current) clearInterval(timerRef.current);
    timerRef.current = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current!);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  // Send OTP
  const { mutate: sendOtp, isPending: isSending } = useAppMutation({
    mutationFn: () => authService.sendOtp({ email }),
    onSuccess: () => {
      setStep("otp");
      setOtp("");
      setOtpError("");
      startCountdown();
    },
    onError: (err: AppError) => setOtpError(err.message),
  });

  // Verify OTP
  const { mutate: verifyOtp, isPending: isVerifying } = useAppMutation({
    mutationFn: () => authService.verifyOtp({ email, otp: otp.trim() }),
    onSuccess: () => {
      if (timerRef.current) clearInterval(timerRef.current);
      setDone(true);
      setTimeout(onVerified, 1200);
    },
    onError: (err: AppError) => setOtpError(err.message),
  });

  const handleSend = () => {
    setOtpError("");
    sendOtp(undefined);
  };

  const handleVerify = (e: React.FormEvent) => {
    e.preventDefault();
    if (otp.trim().length !== 6) {
      setOtpError("Please enter the 6-digit OTP.");
      return;
    }
    setOtpError("");
    verifyOtp(undefined);
  };

  const handleResend = () => {
    setOtp("");
    setOtpError("");
    sendOtp(undefined);
  };

  // Success
  if (done) {
    return (
      <div className="flex items-center gap-2 text-sm text-green-700 bg-green-50 border border-green-100 rounded-lg px-3 py-2">
        <span>✓</span> Email verified successfully!
      </div>
    );
  }

  // Step 1 — initial button
  if (step === "idle") {
    return (
      <div className="space-y-2">
        {otpError && <p className="text-xs text-red-500">{otpError}</p>}
        <button
          onClick={handleSend}
          disabled={isSending}
          className="inline-flex items-center gap-1.5 text-xs font-medium text-amber-700 bg-amber-50 border border-amber-200 px-3 py-1.5 rounded-lg hover:bg-amber-100 transition-colors disabled:opacity-60"
        >
          {isSending ? (
            <>
              <span className="w-3 h-3 border-2 border-amber-600 border-t-transparent rounded-full animate-spin" />
              Sending OTP...
            </>
          ) : (
            "Send OTP"
          )}
        </button>
      </div>
    );
  }

  // Step 2 — OTP input + countdown
  return (
    <form onSubmit={handleVerify} className="space-y-3">
      <p className="text-xs text-gray-500">
        A 6-digit OTP was sent to{" "}
        <span className="font-medium text-gray-700">{email}</span>. Enter it
        below before it expires.
      </p>

      <div className="flex flex-wrap items-center gap-2">
        {/* OTP input */}
        <input
          type="text"
          inputMode="numeric"
          maxLength={6}
          placeholder="000000"
          value={otp}
          onChange={(e) => {
            setOtp(e.target.value.replace(/\D/g, ""));
            if (otpError) setOtpError("");
          }}
          autoFocus
          className="w-32 px-3 py-2 border border-gray-200 rounded-lg text-sm font-mono tracking-widest outline-none focus:ring-2 focus:ring-black text-center"
        />

        {/* Verify button */}
        <button
          type="submit"
          disabled={isVerifying || countdown === 0}
          className="px-4 py-2 bg-black text-white text-sm rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center gap-1.5"
        >
          {isVerifying ? (
            <>
              <span className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              Verifying...
            </>
          ) : (
            "Verify"
          )}
        </button>

        {/* Countdown or resend */}
        {countdown > 0 ? (
          <span
            className={`text-xs font-mono tabular-nums ${countdown < 60 ? "text-red-500" : "text-gray-400"}`}
          >
            {formatCountdown(countdown)}
          </span>
        ) : (
          <button
            type="button"
            onClick={handleResend}
            disabled={isSending}
            className="text-xs text-blue-600 underline hover:text-blue-800 disabled:opacity-50"
          >
            {isSending ? "Resending..." : "Resend OTP"}
          </button>
        )}
      </div>

      {otpError && <p className="text-xs text-red-500">{otpError}</p>}
      {countdown === 0 && (
        <p className="text-xs text-red-500">OTP expired. Please resend.</p>
      )}
    </form>
  );
}

// ── Main Page ─────────────────────────────────────────────────────────────────

export default function ProfilePage() {
  const queryClient = useQueryClient();
  const { user: authUser, setAuth, accessToken, refreshToken } = useAuthStore();

  const [isEditingName, setIsEditingName] = useState(false);
  const [fullName, setFullName] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [nameSuccess, setNameSuccess] = useState("");

  // ── Fetch profile ─────────────────────────────────────────────────────────
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["profile"],
    queryFn: () => userService.getProfile(),
  });

  const profile = data?.data;

  useEffect(() => {
    if (profile) setFullName(profile.fullName);
  }, [profile]);

  // ── Update name ───────────────────────────────────────────────────────────
  const { mutate: update, isPending: isUpdating } = useAppMutation({
    mutationFn: () => userService.updateProfile({ fullName: fullName.trim() }),
    onSuccess: (res) => {
      // Keep Zustand in sync so sidebar shows new name immediately
      if (authUser && accessToken && refreshToken) {
        setAuth(
          { ...authUser, fullName: res.data.fullName },
          accessToken,
          refreshToken,
        );
      }
      queryClient.invalidateQueries({ queryKey: ["profile"] });
      setIsEditingName(false);
      setFieldErrors({});
      setFormError(null);
      setNameSuccess("Name updated.");
      setTimeout(() => setNameSuccess(""), 3000);
    },
    onError: (err: AppError) => {
      if (err.isValidation) setFieldErrors(err.toFieldErrorMap());
      else setFormError(err.message);
    },
  });

  const handleNameSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim()) {
      setFieldErrors({ fullName: "Name is required" });
      return;
    }
    setFieldErrors({});
    setFormError(null);
    update(undefined);
  };

  const cancelEdit = () => {
    setIsEditingName(false);
    setFullName(profile?.fullName ?? "");
    setFieldErrors({});
    setFormError(null);
  };

  const handleEmailVerified = () => {
    queryClient.invalidateQueries({ queryKey: ["profile"] });
  };

  // ── Guards ────────────────────────────────────────────────────────────────
  if (isLoading) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  if (error) {
    return <QueryError error={error} onRetry={refetch} />;
  }

  if (!profile) return null;

  const initials = getInitials(profile.fullName);

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="max-w-xl mx-auto space-y-6">
      {/* Page header */}
      <div>
        <h1 className="text-xl font-semibold text-gray-900">Profile</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Manage your account details.
        </p>
      </div>

      {/* Avatar + inline name edit */}
      <div className="bg-white border border-gray-200 rounded-xl p-6">
        <div className="flex items-start gap-5">
          {/* Avatar circle */}
          <div className="w-16 h-16 rounded-full bg-gray-900 flex items-center justify-center shrink-0">
            <span className="text-white text-xl font-bold tracking-wide">
              {initials}
            </span>
          </div>

          {/* Name / email / badges */}
          <div className="flex-1 min-w-0">
            {/* Inline name edit */}
            {!isEditingName ? (
              <>
                <div className="flex items-center gap-2">
                  <p className="text-base font-semibold text-gray-900 truncate">
                    {profile.fullName}
                  </p>
                  <button
                    onClick={() => setIsEditingName(true)}
                    className="shrink-0 text-xs text-gray-400 hover:text-gray-700 border border-gray-200 px-2 py-0.5 rounded transition-colors"
                  >
                    Edit
                  </button>
                </div>
                {nameSuccess && (
                  <p className="text-xs text-green-600 mt-0.5">
                    ✓ {nameSuccess}
                  </p>
                )}
              </>
            ) : (
              <form onSubmit={handleNameSubmit} className="space-y-2 mb-1">
                <FormError error={formError} />
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={fullName}
                    onChange={(e) => {
                      setFullName(e.target.value);
                      if (fieldErrors.fullName) {
                        setFieldErrors((p) => ({ ...p, fullName: "" }));
                      }
                    }}
                    autoFocus
                    maxLength={100}
                    className="flex-1 min-w-0 px-3 py-1.5 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
                  />
                  <button
                    type="submit"
                    disabled={isUpdating}
                    className="shrink-0 px-3 py-1.5 bg-black text-white text-xs rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center gap-1"
                  >
                    {isUpdating ? (
                      <span className="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    ) : (
                      "Save"
                    )}
                  </button>
                  <button
                    type="button"
                    onClick={cancelEdit}
                    className="shrink-0 px-3 py-1.5 text-xs text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    Cancel
                  </button>
                </div>
                <FieldErrorMessage message={fieldErrors.fullName} />
              </form>
            )}

            <p className="text-sm text-gray-500 truncate mt-1">
              {profile.email}
            </p>

            {/* Badges */}
            <div className="flex flex-wrap items-center gap-2 mt-2">
              <span
                className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                  profile.active
                    ? "bg-green-50 text-green-700"
                    : "bg-red-50 text-red-500"
                }`}
              >
                {profile.active ? "Active" : "Inactive"}
              </span>
              <span
                className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                  profile.isEmailVerified
                    ? "bg-blue-50 text-blue-600"
                    : "bg-amber-50 text-amber-600"
                }`}
              >
                {profile.isEmailVerified ? "✓ Email verified" : "✗ Unverified"}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Account info */}
      <div className="bg-white border border-gray-200 rounded-xl px-5">
        <InfoRow label="Email" value={profile.email} />
        <InfoRow
          label="Account Status"
          value={
            <span
              className={profile.active ? "text-green-600" : "text-red-500"}
            >
              {profile.active ? "Active" : "Inactive"}
            </span>
          }
        />
        <InfoRow
          label="Email Verified"
          value={
            <span
              className={
                profile.isEmailVerified ? "text-blue-600" : "text-amber-600"
              }
            >
              {profile.isEmailVerified ? "Yes" : "No"}
            </span>
          }
        />
      </div>

      {/* Email verification — only shown when not verified */}
      {!profile.isEmailVerified && (
        <div className="bg-white border border-amber-200 rounded-xl p-5">
          <div className="flex items-start gap-3 mb-4">
            <span className="text-amber-500 text-lg leading-none mt-0.5">
              ⚠
            </span>
            <div>
              <h2 className="text-sm font-semibold text-gray-800">
                Verify your email address
              </h2>
              <p className="text-xs text-gray-500 mt-0.5">
                We'll send a 6-digit OTP to{" "}
                <span className="font-medium text-gray-700">
                  {profile.email}
                </span>
                . It expires in 5 minutes.
              </p>
            </div>
          </div>

          <EmailVerification
            email={profile.email}
            onVerified={handleEmailVerified}
          />
        </div>
      )}

      {/* Danger zone */}
      <div className="bg-white border border-red-100 rounded-xl p-5">
        <h2 className="text-sm font-semibold text-red-600 mb-1">Danger Zone</h2>
        <p className="text-xs text-gray-400 mb-4">
          These actions are irreversible. Please proceed with caution.
        </p>
        <button
          disabled
          className="text-sm text-red-500 border border-red-200 px-4 py-2 rounded-lg opacity-40 cursor-not-allowed"
          title="Not implemented"
        >
          Delete Account
        </button>
      </div>
    </div>
  );
}
