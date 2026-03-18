import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    setTokens: (access: string, refresh: string) => void;
    clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            accessToken: null,
            refreshToken: null,
            setTokens: (accessToken, refreshToken) =>
                set({ accessToken, refreshToken }),
            clearAuth: () =>
                set({ accessToken: null, refreshToken: null }),
        }),
        {
            name: 'walletiq-auth',  // localStorage key
            partialize: (state) => ({
                // Only persist tokens, nothing else
                accessToken: state.accessToken,
                refreshToken: state.refreshToken,
            }),
        }
    )
);