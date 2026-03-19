import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type { UserProfileResponse, UpdateProfileRequest } from '../types/user.types';

export const userService = {
  getProfile: async (): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.get('/users/me');
    return res.data;
  },

  updateProfile: async (data: UpdateProfileRequest): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.patch('/users/me', data);
    return res.data;
  },
};