import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthUserResponse } from '../types/auth.types';

interface AuthState {
    user: AuthUserResponse | null;
    accessToken: string | null;
    refreshToken: string | null;
    setAuth: (user: AuthUserResponse, access: string, refresh: string) => void;
    clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            accessToken: null,
            refreshToken: null,
            setAuth: (user, accessToken, refreshToken) =>
                set({ user, accessToken, refreshToken }),
            clearAuth: () =>
                set({ user: null, accessToken: null, refreshToken: null }),
        }),
        {
            name: 'walletiq-auth',
            partialize: (state) => ({
                user: state.user,
                accessToken: state.accessToken,
                refreshToken: state.refreshToken,
            }),
        }
    )
);