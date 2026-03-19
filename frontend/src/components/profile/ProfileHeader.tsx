import { useState, useRef } from "react";
import { AppError } from "../../errors/AppError";
import { FieldErrorMessage } from "../ui/FieldErrorMessage";
import { FormError } from "../ui/FormError";
import type { UserProfileResponse } from "../../types/user.types";
import { getInitials } from "../../utils/profileHelpers";

interface Props {
  profile: UserProfileResponse;
  isUpdating: boolean;
  onUpdateName: (name: string) => void;
  nameSuccess: string;
  updateError: AppError | null;
}

/**
 * The avatar straddles the dark/white boundary via negative margin (-mt-8).
 * Name and email are fully below, always readable on white.
 */
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

  // Photo upload ref — ready for future implementation
  const fileInputRef = useRef<HTMLInputElement>(null);

  const initials = getInitials(profile.fullName);

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
    <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
      {/* ── Dark decorative band ── */}
      {/* Short enough that avatar clearly overlaps it */}
      <div className="bg-[#0f0f0f] h-24 relative">
        {/* Dot grid texture — same as homepage hero */}
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            backgroundImage:
              "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
            backgroundSize: "24px 24px",
          }}
        />
      </div>

      {/* ── White content area ── */}
      <div className="px-6 pb-6">
        {/* Avatar row — pulled up to overlap the dark band */}
        <div className="flex items-end gap-4 -mt-8 mb-4">
          {/* Avatar — accent yellow circle, photo-ready */}
          <div className="relative shrink-0 group">
            <div className="w-16 h-16 rounded-full bg-[#e8ff4f] flex items-center justify-center shadow-md border-4 border-white overflow-hidden">
              {/* Swap for <img src={photoUrl} /> when photo upload is ready */}
              <span className="text-gray-900 text-lg font-black tracking-tight select-none">
                {initials}
              </span>
            </div>

            {/* Camera icon overlay — triggers file picker (wired when backend ready) */}
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              className="absolute inset-0 rounded-full bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
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

            {/* Hidden file input — TODO: wire to upload API */}
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

          {/* Name + email — fully in the white zone, always readable */}
          {/* pt-8 pushes this text down so it clears the dark band boundary */}
          <div className="min-w-0 pt-9">
            <p className="text-base font-bold text-gray-900 truncate leading-tight">
              {profile.fullName}
            </p>
            <p className="text-sm text-gray-400 truncate mt-0.5">
              {profile.email}
            </p>
          </div>
        </div>

        {/* ── Status badges ── */}
        <div className="flex flex-wrap items-center gap-2 mb-5">
          <span
            className={`text-xs font-medium px-2.5 py-0.5 rounded-full border ${
              profile.active
                ? "bg-green-50 text-green-700 border-green-100"
                : "bg-red-50 text-red-600 border-red-100"
            }`}
          >
            {profile.active ? "Active" : "Inactive"}
          </span>
          <span
            className={`text-xs font-medium px-2.5 py-0.5 rounded-full border ${
              profile.isEmailVerified
                ? "bg-blue-50 text-blue-600 border-blue-100"
                : "bg-amber-50 text-amber-600 border-amber-100"
            }`}
          >
            {profile.isEmailVerified
              ? "✓ Email verified"
              : "✗ Unverified email"}
          </span>
        </div>

        {/* ── Inline name edit ── */}
        {!isEditing ? (
          <div className="flex items-center gap-3">
            {nameSuccess && (
              <p className="text-xs text-green-600 font-medium">
                ✓ {nameSuccess}
              </p>
            )}
            <button
              onClick={() => setIsEditing(true)}
              className="text-xs text-gray-500 border border-gray-200 px-3 py-1.5 rounded-lg hover:bg-gray-50 hover:text-gray-900 transition-colors"
            >
              Edit name
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-2">
            {updateError && !updateError.isValidation && (
              <FormError error={updateError.message} />
            )}
            <div className="flex items-center gap-2">
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
              <button
                type="submit"
                disabled={isUpdating}
                className="shrink-0 px-4 py-2 bg-gray-900 text-white text-xs font-medium rounded-lg hover:bg-black disabled:opacity-60 transition-colors flex items-center gap-1.5"
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
                className="shrink-0 px-4 py-2 text-xs text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
            </div>
            <FieldErrorMessage message={fieldError} />
          </form>
        )}
      </div>
    </div>
  );
};

export default ProfileHeader;
