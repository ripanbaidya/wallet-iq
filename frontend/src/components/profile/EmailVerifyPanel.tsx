import { useState, useEffect, useRef } from "react";
import { AppError } from "../../errors/AppError";
import { useAppMutation } from "../../hooks/useAppMutation";
import { authService } from "../../services/authService";
import {
  OTP_EXPIRY_SECONDS,
  formatCountdown,
} from "../../utils/profileHelpers";

interface Props {
  open: boolean;
  email: string;
  onClose: () => void;
  onVerified: () => void;
}

type Step = "idle" | "otp";

const EmailVerifyPanel: React.FC<Props> = ({
  open,
  email,
  onClose,
  onVerified,
}) => {
  const [step, setStep] = useState<Step>("idle");
  const [otp, setOtp] = useState("");
  const [countdown, setCountdown] = useState(0);
  const [otpError, setOtpError] = useState("");
  const [done, setDone] = useState(false);

  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (open) {
      setStep("idle");
      setOtp("");
      setOtpError("");
      setDone(false);
      setCountdown(0);
    }
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [open]);

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

  const { mutate: verifyOtp, isPending: isVerifying } = useAppMutation({
    mutationFn: () => authService.verifyOtp({ email, otp: otp.trim() }),
    onSuccess: () => {
      if (timerRef.current) clearInterval(timerRef.current);
      setDone(true);
      setTimeout(() => {
        onVerified();
        onClose();
      }, 1400);
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

  if (!open) return null;

  // Progress indicator — 0→1 as countdown decreases
  const progressPct =
    countdown > 0
      ? ((OTP_EXPIRY_SECONDS - countdown) / OTP_EXPIRY_SECONDS) * 100
      : 100;

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/40 z-30" onClick={onClose} />

      {/*
        Panel layout:
          Mobile  (< sm) : bottom sheet, max-h-[92dvh], rounded-t-2xl
          sm+            : right-side drawer, full height, max-w-md
      */}
      <div
        className="
          fixed z-40 flex flex-col bg-white shadow-2xl
          bottom-0 left-0 right-0 max-h-[92dvh] rounded-t-2xl
          sm:inset-y-0 sm:right-0 sm:left-auto sm:bottom-auto
          sm:w-full sm:max-w-md sm:max-h-none sm:rounded-none
        "
      >
        {/* ── Header ── */}
        <div className="flex items-center justify-between px-5 sm:px-6 py-4 border-b border-gray-100 shrink-0">
          <div>
            <h2 className="text-sm sm:text-base font-semibold text-gray-900">
              Verify your email
            </h2>
            <p className="text-xs text-gray-400 mt-0.5 truncate max-w-[220px] sm:max-w-none">
              {email}
            </p>
          </div>
          <button
            onClick={onClose}
            className="ml-4 shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-gray-400 hover:text-gray-700 hover:bg-gray-100 transition-colors"
          >
            <svg
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
            >
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        {/* ── Body (scrollable) ── */}
        <div className="flex-1 overflow-y-auto overscroll-contain px-5 sm:px-6 py-5 sm:py-6 space-y-5">
          {/* Success state */}
          {done && (
            <div className="flex items-center gap-3 bg-green-50 border border-green-200 rounded-2xl px-4 py-4">
              <div className="w-9 h-9 rounded-full bg-green-100 flex items-center justify-center shrink-0">
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="#16a34a"
                  strokeWidth="2.5"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-semibold text-green-800">
                  Email verified!
                </p>
                <p className="text-xs text-green-600 mt-0.5">
                  Your account is now fully verified.
                </p>
              </div>
            </div>
          )}

          {/* ── Step 1 — explanation ── */}
          {!done && step === "idle" && (
            <div className="space-y-4">
              <div className="bg-[#0f0f0f] rounded-2xl px-5 py-5 relative overflow-hidden">
                <div
                  className="absolute inset-0 pointer-events-none opacity-60"
                  style={{
                    backgroundImage:
                      "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
                    backgroundSize: "20px 20px",
                  }}
                />
                <div className="absolute -top-4 -right-4 w-24 h-24 bg-[#e8ff4f]/10 rounded-full blur-2xl pointer-events-none" />
                <div className="relative">
                  <div className="w-10 h-10 rounded-xl bg-[#e8ff4f]/10 border border-[#e8ff4f]/20 flex items-center justify-center mb-3">
                    <span className="text-[#e8ff4f] text-lg">✉</span>
                  </div>
                  <p className="text-white text-sm font-semibold">
                    Verify your email address
                  </p>
                  <p className="text-white/50 text-xs mt-1.5 leading-relaxed">
                    We'll send a 6-digit one-time password to{" "}
                    <span className="text-white/80 font-medium">{email}</span>.
                    It expires in 5 minutes.
                  </p>
                </div>
              </div>

              {otpError && (
                <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-xl px-3 py-2.5">
                  {otpError}
                </p>
              )}

              <button
                onClick={handleSend}
                disabled={isSending}
                className="w-full py-3 bg-gray-900 text-white text-sm font-semibold rounded-xl hover:bg-black disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
              >
                {isSending ? (
                  <>
                    <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Sending OTP...
                  </>
                ) : (
                  "Send OTP →"
                )}
              </button>
            </div>
          )}

          {/* ── Step 2 — OTP input ── */}
          {!done && step === "otp" && (
            <form onSubmit={handleVerify} className="space-y-5">
              <p className="text-sm text-gray-500 leading-relaxed">
                Enter the 6-digit code sent to{" "}
                <span className="font-medium text-gray-900">{email}</span>.
              </p>

              {/* OTP input */}
              <div>
                <label className="block text-xs text-gray-400 uppercase tracking-widest mb-2">
                  One-time password
                </label>
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
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl text-2xl font-mono tracking-[0.5em] outline-none focus:ring-2 focus:ring-black text-center"
                />
                {otpError && (
                  <p className="mt-2 text-xs text-red-500">{otpError}</p>
                )}
              </div>

              {/* Progress bar + countdown */}
              <div className="space-y-1.5">
                <div className="h-1 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-1000 ${
                      countdown < 60 ? "bg-red-400" : "bg-gray-900"
                    }`}
                    style={{ width: `${100 - progressPct}%` }}
                  />
                </div>
                <div className="flex items-center justify-between text-xs">
                  <span className="text-gray-400">Time remaining</span>
                  <span
                    className={`font-mono font-semibold tabular-nums ${
                      countdown < 60 ? "text-red-500" : "text-gray-700"
                    }`}
                  >
                    {formatCountdown(countdown)}
                  </span>
                </div>
              </div>

              {/* Expired notice */}
              {countdown === 0 && (
                <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3 flex items-center justify-between gap-3">
                  <p className="text-xs text-amber-700">
                    OTP expired. Request a new one.
                  </p>
                  <button
                    type="button"
                    onClick={handleResend}
                    disabled={isSending}
                    className="shrink-0 text-xs font-semibold text-amber-700 underline underline-offset-2 disabled:opacity-50"
                  >
                    {isSending ? "Sending..." : "Resend"}
                  </button>
                </div>
              )}

              {/* Verify button */}
              <button
                type="submit"
                disabled={isVerifying || countdown === 0}
                className="w-full py-3 bg-gray-900 text-white text-sm font-semibold rounded-xl hover:bg-black disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
              >
                {isVerifying ? (
                  <>
                    <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Verifying...
                  </>
                ) : (
                  "Verify email"
                )}
              </button>

              {/* Resend while active */}
              {countdown > 0 && (
                <p className="text-center text-xs text-gray-400">
                  Didn't receive it?{" "}
                  <button
                    type="button"
                    onClick={handleResend}
                    disabled={isSending}
                    className="text-gray-700 underline underline-offset-2 hover:text-black disabled:opacity-50"
                  >
                    Resend OTP
                  </button>
                </p>
              )}
            </form>
          )}
        </div>

        {/* ── Footer ── */}
        <div className="px-5 sm:px-6 py-4 pb-[calc(1rem_+_env(safe-area-inset-bottom))] border-t border-gray-100 shrink-0">
          <button
            type="button"
            onClick={onClose}
            className="w-full py-2.5 text-sm text-gray-600 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </>
  );
};

export default EmailVerifyPanel;
