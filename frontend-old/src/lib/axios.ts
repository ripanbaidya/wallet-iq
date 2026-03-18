import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { parseError } from '../errors/errorUtils';
import { useAuthStore } from '../store/authStore';

export const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
    headers: { 'Content-Type': 'application/json' },
    timeout: 15_000,
});

// Attaches the JWT access token to every request automatically
apiClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = useAuthStore.getState().accessToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(parseError(error))
);

// Successful responses pass through as-is
// Error responses are normalized into AppError and re-thrown
apiClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const appError = parseError(error);

        // Handle token expiry globally — redirect to login
        if (
            appError.isAuthentication &&
            (appError.code === 'TOKEN_EXPIRED' || appError.code === 'UNAUTHENTICATED')
        ) {
            useAuthStore.getState().clearAuth();
            // Avoid importing React Router here — use a nav event instead
            window.dispatchEvent(new CustomEvent('auth:unauthorized'));
        }

        // Re-throw as AppError so every caller gets a typed, normalized error
        return Promise.reject(appError);
    }
);