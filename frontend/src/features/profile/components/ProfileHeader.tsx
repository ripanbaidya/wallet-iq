import { useState, useRef } from "react";
import { AppError } from "../../../api/errorParser";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";
import type { UserProfileResponse } from "../profile.types";
import { getAvatarUrl } from "../../../shared/utils/profileHelpers";

interface Props {
  profile: UserProfileResponse;
  isUpdating: boolean;
  onUpdateName: (name: string) => void;
  nameSuccess: string;
  updateError: AppError | null;
}

const ProfileHeader: React.FC<Props> = ({
  profile,
  isUpdating,
  onUpdateName,
  nameSuccess,
  updateError,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [fullName, setFullName] = useState(profile.fullName);
  const [fieldError, setFieldError] = useState("");

  const fileInputRef = useRef<HTMLInputElement>(null);
  const avatarUrl = getAvatarUrl({
    id: profile.id,
    email: profile.email,
    fullName: profile.fullName,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim()) {
      setFieldError("Name is required");
      return;
    }
    setFieldError("");
    onUpdateName(fullName.trim());
    setIsEditing(false);
  };

  const handleCancel = () => {
    setFullName(profile.fullName);
    setFieldError("");
    setIsEditing(false);
  };

  return (
    <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
      {/* ── Dark decorative band ── */}
      <div className="bg-[#0f0f0f] h-24 sm:h-28 relative">
        <div
          className="absolute inset-0 pointer-events-none opacity-60"
          style={{
            backgroundImage:
              "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
            backgroundSize: "20px 20px",
          }}
        />
        {/* Accent glow */}
        <div className="absolute -bottom-4 -left-4 w-32 h-32 bg-[#e8ff4f]/10 rounded-full blur-2xl pointer-events-none" />
      </div>

      {/* ── White content area ── */}
      <div className="px-4 sm:px-6 pb-5 sm:pb-6">
        {/* Avatar + identity row */}
        <div className="flex flex-col sm:flex-row sm:items-end gap-3 sm:gap-4 -mt-8 sm:-mt-9 mb-4 sm:mb-5">
          {/* Avatar */}
          <div className="relative shrink-0 group self-start">
            <img
              src={avatarUrl}
              alt={profile.fullName}
              className="w-16 h-16 sm:w-18 sm:h-18 rounded-2xl shadow-lg border-4 border-white object-cover"
            />
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              className="absolute inset-0 rounded-2xl bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
              title="Change photo (coming soon)"
            >
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="white"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" />
                <circle cx="12" cy="13" r="4" />
              </svg>
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={() => {
                // TODO: wire to photo upload API when available
              }}
            />
          </div>

          {/* Name + email */}
          <div className="min-w-0 pt-2 sm:pt-9 flex-1">
            <p className="text-base sm:text-lg font-bold text-gray-900 truncate leading-tight">
              {profile.fullName}
            </p>
            <p className="text-xs sm:text-sm text-gray-400 truncate mt-0.5">
              {profile.email}
            </p>
          </div>
        </div>

        {/* ── Status badges ── */}
        <div className="flex flex-wrap items-center gap-2 mb-4 sm:mb-5">
          <span
            className={`inline-flex items-center gap-1 text-xs font-medium px-2.5 py-1 rounded-full border ${
              profile.active
                ? "bg-green-50 text-green-700 border-green-200"
                : "bg-red-50 text-red-600 border-red-200"
            }`}
          >
            <span
              className={`w-1.5 h-1.5 rounded-full ${
                profile.active ? "bg-green-500" : "bg-red-500"
              }`}
            />
            {profile.active ? "Active" : "Inactive"}
          </span>
          <span
            className={`inline-flex items-center gap-1 text-xs font-medium px-2.5 py-1 rounded-full border ${
              profile.isEmailVerified
                ? "bg-blue-50 text-blue-600 border-blue-200"
                : "bg-amber-50 text-amber-600 border-amber-200"
            }`}
          >
            {profile.isEmailVerified ? (
              <svg
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            ) : (
              <svg
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
              >
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            )}
            {profile.isEmailVerified ? "Email verified" : "Unverified email"}
          </span>
        </div>

        {/* ── Inline name edit ── */}
        {!isEditing ? (
          <div className="flex flex-wrap items-center gap-2">
            {nameSuccess && (
              <span className="inline-flex items-center gap-1.5 text-xs text-green-700 bg-green-50 border border-green-200 px-3 py-1.5 rounded-lg font-medium">
                <svg
                  width="10"
                  height="10"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="3"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polyline points="20 6 9 17 4 12" />
                </svg>
                {nameSuccess}
              </span>
            )}
            <button
              onClick={() => setIsEditing(true)}
              className="inline-flex items-center gap-1.5 text-xs text-gray-600 border border-gray-200 px-3 py-1.5 rounded-lg hover:bg-gray-50 hover:text-gray-900 hover:border-gray-300 transition-colors"
            >
              <svg
                width="11"
                height="11"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
              </svg>
              Edit name
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-2">
            {updateError && !updateError.isValidation && (
              <FormError error={updateError.message} />
            )}
            <div className="flex flex-col xs:flex-row items-stretch xs:items-center gap-2">
              <input
                type="text"
                value={fullName}
                onChange={(e) => {
                  setFullName(e.target.value);
                  if (fieldError) setFieldError("");
                }}
                autoFocus
                maxLength={100}
                placeholder="Full name"
                className="flex-1 min-w-0 px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
              />
              <div className="flex gap-2 shrink-0">
                <button
                  type="submit"
                  disabled={isUpdating}
                  className="flex-1 xs:flex-none px-4 py-2 bg-gray-900 text-white text-xs font-medium rounded-lg hover:bg-black disabled:opacity-60 transition-colors flex items-center justify-center gap-1.5"
                >
                  {isUpdating ? (
                    <span className="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  ) : (
                    "Save"
                  )}
                </button>
                <button
                  type="button"
                  onClick={handleCancel}
                  className="flex-1 xs:flex-none px-4 py-2 text-xs text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
              </div>
            </div>
            <FieldErrorMessage message={fieldError} />
          </form>
        )}
      </div>
    </div>
  );
};

export default ProfileHeader;
