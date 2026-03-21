/**
 * Note:
 * Right now we are hard coding the otp expiry, backend also have '5' minutes
 * for otp verification, we will update this in future.
 */
export const OTP_EXPIRY_SECONDS = 5 * 60;

/**
 * Returns up to 2 uppercase initials from a full name.
 * e.g. "Ripan Baidya" → "RB", "Ripan" → "R"
 */
export const getInitials = (name: string): string =>
  name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join("");

/**
 * Formats a seconds value as MM:SS.
 * e.g. 305 → "05:05"
 */
export const formatCountdown = (seconds: number): string => {
  const m = Math.floor(seconds / 60).toString().padStart(2, "0");
  const s = (seconds % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
};