import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type { PaginatedUsers, UserProfileResponse, Role } from '../types/admin.types';

export const adminService = {
  getUsers: async (page = 0, size = 10): Promise<ResponseWrapper<PaginatedUsers>> => {
    const res = await apiClient.get<ResponseWrapper<PaginatedUsers>>(
      `/admin/users?page=${page}&size=${size}`
    );
    return res.data;
  },

  getUserById: async (id: string): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.get<ResponseWrapper<UserProfileResponse>>(`/admin/users/${id}`);
    return res.data;
  },

  getUserCount: async (role: Role, active: boolean): Promise<ResponseWrapper<number>> => {
    const res = await apiClient.get<ResponseWrapper<number>>(
      `/admin/users/count?role=${role}&active=${active}`
    );
    return res.data;
  },
};