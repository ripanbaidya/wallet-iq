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

  const [verifyPanelOpen, setVerifyPanelOpen] = useState(false);
  const [nameSuccess, setNameSuccess] = useState("");
  const [updateError, setUpdateError] = useState<AppError | null>(null);

  /* Fetch profile */
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["profile"],
    queryFn: () => userService.getProfile(),
  });

  const profile = data?.data;

  /* Update name */
  const { mutate: updateName, isPending: isUpdating } = useAppMutation({
    mutationFn: (fullName: string) => userService.updateProfile({ fullName }),
    onSuccess: (res) => {
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

  return (
    <div className="max-w-xl mx-auto px-4 sm:px-6 lg:px-0 py-2 sm:py-4 space-y-3 sm:space-y-4">
      {/* Page heading */}
      <div className="mb-1">
        <h1 className="text-xl font-semibold text-gray-900">Profile</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Manage your account details.
        </p>
      </div>

      <ProfileHeader
        profile={profile}
        isUpdating={isUpdating}
        onUpdateName={(name) => updateName(name)}
        nameSuccess={nameSuccess}
        updateError={updateError}
      />

      <AccountInfoCard
        profile={profile}
        onOpenVerify={() => setVerifyPanelOpen(true)}
      />

      <DangerZone />

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
