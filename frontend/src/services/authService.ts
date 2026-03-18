import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type { LoginRequest, LoginResponse, RegisterRequest } from '../types/auth.types';

export const authService = {
    login: async (data: LoginRequest): Promise<ResponseWrapper<LoginResponse>> => {
        const res = await apiClient.post<ResponseWrapper<LoginResponse>>('/auth/login', data);
        return res.data;
    },

    register: async (data: RegisterRequest): Promise<ResponseWrapper<void>> => {
        const res = await apiClient.post<ResponseWrapper<void>>('/auth/register', data);
        return res.data;
    },

    logout: async (): Promise<void> => {
        await apiClient.post('/auth/logout');
    },
};