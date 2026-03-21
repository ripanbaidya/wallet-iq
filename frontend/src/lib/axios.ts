import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { parseError } from '../shared/utils/errorUtils';
import { useAuthStore } from '../store/authStore';

/**
 * Centralized Axios instance for all API calls.
 *
 * Responsibilities:
 * - Configure base URL and default headers
 * - Attach authentication token automatically
 * - Normalize all errors into AppError
 * - Handle global auth failures (e.g., token expiry)
 */
export const apiClient = axios.create({
    // Uses environment variable if available, otherwise falls back to local backend
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',

    // Default headers applied to every request
    headers: { 'Content-Type': 'application/json' },

    // Request timeout (15 seconds)
    timeout: 15_000,
});

/**
 * Request Interceptor
 *
 * Injects JWT access token into Authorization header
 * before every outgoing request.
 */
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

/**
 * Response Interceptor
 *
 * - Passes successful responses as-is
 * - Transforms all errors into a consistent AppError
 * - Handles authentication failures globally
 */
apiClient.interceptors.response.use(
    (response) => response,

    async (error: AxiosError) => {
        const appError = parseError(error);

        /**
         * Global authentication handling:
         * - Clears stored auth state
         * - Emits an event for UI to redirect to login
         */
        if (
            appError.isAuthentication &&
            (appError.code === 'TOKEN_EXPIRED' || appError.code === 'UNAUTHENTICATED')
        ) {
            useAuthStore.getState().clearAuth();

            // Decoupled navigation trigger (no direct router dependency)
            window.dispatchEvent(new CustomEvent('auth:unauthorized'));
        }

        // Always reject with normalized error
        return Promise.reject(appError);
    }
);