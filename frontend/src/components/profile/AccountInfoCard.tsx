import type { UserProfileResponse } from "../../types/user.types";

interface Props {
  profile: UserProfileResponse;
  /** Called when user clicks "Verify email" — opens the slide panel */
  onOpenVerify: () => void;
}

/**
 * Reusable label + value row with a bottom hairline.
 * `last:border-0` removes the border on the last row.
 */
const InfoRow: React.FC<{
  label: string;
  value: React.ReactNode;
  action?: React.ReactNode;
}> = ({ label, value, action }) => (
  <div className="flex items-center justify-between py-3.5 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500">{label}</span>
    <div className="flex items-center gap-3">
      <span className="text-sm text-gray-900 font-medium text-right">
        {value}
      </span>
      {action}
    </div>
  </div>
);

/**
 * It will Shows email, account status and email-verified status in a clean list.
 * If the email is not verified, a small "Verify now" button appears inline
 * that opens the EmailVerifyPanel.
 */
const AccountInfoCard: React.FC<Props> = ({ profile, onOpenVerify }) => (
  <div className="bg-white border border-gray-200 rounded-xl px-5">
    {/* Section label — matches homepage eyebrow style */}
    <p className="text-xs text-gray-400 uppercase tracking-widest pt-4 pb-1">
      Account details
    </p>

    <InfoRow label="Email" value={profile.email} />

    <InfoRow
      label="Account status"
      value={
        <span
          className={
            profile.active
              ? "text-green-600 font-medium"
              : "text-red-500 font-medium"
          }
        >
          {profile.active ? "Active" : "Inactive"}
        </span>
      }
    />

    <InfoRow
      label="Email verified"
      value={
        <span
          className={
            profile.isEmailVerified
              ? "text-blue-600 font-medium"
              : "text-amber-600 font-medium"
          }
        >
          {profile.isEmailVerified ? "Yes" : "No"}
        </span>
      }
      action={
        /* Only show "Verify now" when not yet verified */
        !profile.isEmailVerified ? (
          <button
            onClick={onOpenVerify}
            className="text-xs font-medium text-gray-900 border border-gray-900 px-2.5 py-0.5 rounded-lg hover:bg-gray-900 hover:text-white transition-colors"
          >
            Verify now
          </button>
        ) : null
      }
    />
  </div>
);

export default AccountInfoCard;
