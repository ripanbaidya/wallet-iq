export interface UserProfileResponse {
  id: string;
  fullName: string;
  email: string;
  active: boolean;
  isEmailVerified: boolean;
}

export interface UpdateProfileRequest {
  fullName: string;
}