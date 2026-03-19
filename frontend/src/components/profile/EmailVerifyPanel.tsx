import { useState, useEffect, useRef } from "react";
import { AppError } from "../../errors/AppError";
import { useAppMutation } from "../../hooks/useAppMutation";
import { authService } from "../../services/authService";
import { OTP_EXPIRY_SECONDS, formatCountdown } from "../../utils/profileHelpers";

interface Props {
  open: boolean;
  email: string;
  onClose: () => void;
  onVerified: () => void;
}

type Step = "idle" | "otp";

/**
 * EmailVerifyPanel
 *
 * A right-side slide-in panel (identical structure to TransactionForm).
 * Two-step flow:
 *   Step 1 (idle) — explain what will happen, one "Send OTP" button.
 *   Step 2 (otp)  — OTP input + countdown timer + resend option.
 *
 * Closes and calls onVerified() on success.
 */
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

  // Reset internal state every time the panel opens
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

  // ── Countdown timer ───────────────────────────────────────────────────────
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

  // ── Send OTP mutation ─────────────────────────────────────────────────────
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

  // ── Verify OTP mutation ───────────────────────────────────────────────────
  const { mutate: verifyOtp, isPending: isVerifying } = useAppMutation({
    mutationFn: () => authService.verifyOtp({ email, otp: otp.trim() }),
    onSuccess: () => {
      if (timerRef.current) clearInterval(timerRef.current);
      setDone(true);
      // Brief success moment then close + notify parent
      setTimeout(() => {
        onVerified();
        onClose();
      }, 1400);
    },
    onError: (err: AppError) => setOtpError(err.message),
  });

  // ── Handlers ──────────────────────────────────────────────────────────────
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

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/30 z-30" onClick={onClose} />

      {/* Slide-in panel — same dimensions and structure as TransactionForm */}
      <div className="fixed inset-y-0 right-0 w-full max-w-md bg-white shadow-xl z-40 flex flex-col">
        {/* ── Panel header ── */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <div>
            <h2 className="text-base font-semibold text-gray-900">
              Verify your email
            </h2>
            <p className="text-xs text-gray-400 mt-0.5">{email}</p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 transition-colors text-xl leading-none"
          >
            ✕
          </button>
        </div>

        {/* ── Panel body ── */}
        <div className="flex-1 overflow-y-auto px-6 py-6 space-y-6">
          {/* Success state */}
          {done && (
            <div className="flex items-center gap-3 bg-green-50 border border-green-100 rounded-xl px-4 py-4">
              <span className="text-green-500 text-xl">✓</span>
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

          {/* Step 1 — explanation + send button */}
          {!done && step === "idle" && (
            <div className="space-y-5">
              {/* Dark info block — mirrors homepage hero aesthetic */}
              <div className="bg-[#0f0f0f] rounded-xl px-5 py-5 relative overflow-hidden">
                <div
                  className="absolute inset-0 pointer-events-none"
                  style={{
                    backgroundImage:
                      "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
                    backgroundSize: "20px 20px",
                  }}
                />
                <div className="relative">
                  <span className="text-[#e8ff4f] text-2xl font-black leading-none">
                    ✉
                  </span>
                  <p className="text-white text-sm font-semibold mt-3">
                    Verify your email address
                  </p>
                  <p className="text-white/50 text-xs mt-1 leading-relaxed">
                    We'll send a 6-digit one-time password to{" "}
                    <span className="text-white/80 font-medium">{email}</span>.
                    It expires in 5 minutes.
                  </p>
                </div>
              </div>

              {/* Error (e.g. rate limit) */}
              {otpError && (
                <p className="text-xs text-red-500 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
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

          {/* Step 2 — OTP input + countdown */}
          {!done && step === "otp" && (
            <form onSubmit={handleVerify} className="space-y-5">
              <p className="text-sm text-gray-500 leading-relaxed">
                Enter the 6-digit code sent to{" "}
                <span className="font-medium text-gray-900">{email}</span>.
              </p>

              {/* Large OTP input */}
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

              {/* Countdown display */}
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

              {/* Expired notice + resend */}
              {countdown === 0 && (
                <div className="bg-amber-50 border border-amber-100 rounded-xl px-4 py-3 flex items-center justify-between">
                  <p className="text-xs text-amber-700">
                    OTP expired. Request a new one.
                  </p>
                  <button
                    type="button"
                    onClick={handleResend}
                    disabled={isSending}
                    className="text-xs font-semibold text-amber-700 underline underline-offset-2 disabled:opacity-50"
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

              {/* Resend link (while countdown is still running) */}
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

        {/* ── Panel footer ── */}
        <div className="px-6 py-4 border-t border-gray-100">
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
