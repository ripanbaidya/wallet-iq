/**
 * ProfilePage.tsx
 *
 * Thin orchestrator — all data fetching and mutations live here.
 * UI is split across focused components in components/profile/:
 *
 *   ProfileHeader     — avatar (photo-ready), name edit, badges
 *   AccountInfoCard   — email / status / verified info rows
 *   EmailVerifyPanel  — slide-in OTP flow (like TransactionForm)
 *   DangerZone        — delete account (disabled until backend ready)
 *   profileHelpers    — pure utilities (getInitials, formatCountdown, constants)
 */

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { userService } from "../../services/userService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";
import { useAuthStore } from "../../store/authStore";

import ProfileHeader from "../../components/profile/ProfileHeader";
import AccountInfoCard from "../../components/profile/AccountInfoCard";
import EmailVerifyPanel from "../../components/profile/EmailVerifyPanel";
import DangerZone from "../../components/profile/DangerZone";

export default function ProfilePage() {
  const queryClient = useQueryClient();
  const { user: authUser, setAuth, accessToken, refreshToken } = useAuthStore();

  // ── Panel state ───────────────────────────────────────────────────────────
  const [verifyPanelOpen, setVerifyPanelOpen] = useState(false);

  // ── Name update feedback ──────────────────────────────────────────────────
  const [nameSuccess, setNameSuccess] = useState("");
  const [updateError, setUpdateError] = useState<AppError | null>(null);

  // ── Fetch profile ─────────────────────────────────────────────────────────
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["profile"],
    queryFn: () => userService.getProfile(),
  });

  const profile = data?.data;

  // ── Update name ───────────────────────────────────────────────────────────
  const { mutate: updateName, isPending: isUpdating } = useAppMutation({
    mutationFn: (fullName: string) => userService.updateProfile({ fullName }),
    onSuccess: (res) => {
      // Keep Zustand in sync so sidebar shows the new name immediately
      if (authUser && accessToken && refreshToken) {
        setAuth(
          { ...authUser, fullName: res.data.fullName },
          accessToken,
          refreshToken,
        );
      }
      queryClient.invalidateQueries({ queryKey: ["profile"] });
      setUpdateError(null);
      setNameSuccess("Name updated successfully.");
      setTimeout(() => setNameSuccess(""), 3000);
    },
    onError: (err: AppError) => setUpdateError(err),
  });

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

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="max-w-xl mx-auto space-y-4">
      {/* Page heading — consistent with other app pages */}
      <div>
        <h1 className="text-xl font-semibold text-gray-900">Profile</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Manage your account details.
        </p>
      </div>

      {/* Avatar + name edit + badges */}
      <ProfileHeader
        profile={profile}
        isUpdating={isUpdating}
        onUpdateName={(name) => updateName(name)}
        nameSuccess={nameSuccess}
        updateError={updateError}
      />

      {/* Email / status / verified rows */}
      <AccountInfoCard
        profile={profile}
        onOpenVerify={() => setVerifyPanelOpen(true)}
      />

      {/* Danger zone */}
      <DangerZone />

      {/* Email verification slide-in panel */}
      <EmailVerifyPanel
        open={verifyPanelOpen}
        email={profile.email}
        onClose={() => setVerifyPanelOpen(false)}
        onVerified={() => {
          queryClient.invalidateQueries({ queryKey: ["profile"] });
          setVerifyPanelOpen(false);
        }}
      />
    </div>
  );
}
