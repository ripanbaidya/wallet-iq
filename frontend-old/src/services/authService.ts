import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type { AuthResponse, LoginRequest, SignupRequest } from '../types/auth.types';

export const authService = {
    login: async (data: LoginRequest): Promise<ResponseWrapper<AuthResponse>> => {
        const res = await apiClient.post<ResponseWrapper<AuthResponse>>('/auth/login', data);
        return res.data;
    },

    signup: async (data: SignupRequest): Promise<ResponseWrapper<void>> => {
        const res = await apiClient.post<ResponseWrapper<void>>('/auth/signup', data);
        return res.data;
    },

    logout: async (): Promise<void> => {
        await apiClient.post('/auth/logout');
    },
};