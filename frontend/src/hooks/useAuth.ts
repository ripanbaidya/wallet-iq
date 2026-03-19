import { useAuthStore } from '../store/authStore';

export const useAuth = () => {
    const { user, accessToken, isAdmin, setAuth, setIsAdmin, clearAuth } = useAuthStore();
    return {
        user,
        accessToken,
        isAdmin,
        isAuthenticated: !!user && !!accessToken,
        setAuth,
        setIsAdmin,
        logout: clearAuth,
    };
};