import React, { createContext, useState, useEffect, useCallback } from "react";
import type { AuthUser } from "../types/auth.types";

interface AuthContextType {
    user: AuthUser | null;
    accessToken: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (user: AuthUser, accessToken: string, refreshToken: string) => void;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

const ACCESS_TOKEN_KEY = "walletiq_access_token";
const REFRESH_TOKEN_KEY = "walletiq_refresh_token";
const USER_KEY = "walletiq_user";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const storedToken = localStorage.getItem(ACCESS_TOKEN_KEY);
        const storedUser = localStorage.getItem(USER_KEY);

        if (storedToken && storedUser) {
            try {
                setAccessToken(storedToken);
                setUser(JSON.parse(storedUser) as AuthUser);
            } catch {
                localStorage.removeItem(ACCESS_TOKEN_KEY);
                localStorage.removeItem(USER_KEY);
            }
        }
        setIsLoading(false);
    }, []);

    const login = useCallback((userData: AuthUser, token: string, refreshToken: string) => {
        setUser(userData);
        setAccessToken(token);
        localStorage.setItem(ACCESS_TOKEN_KEY, token);
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
        localStorage.setItem(USER_KEY, JSON.stringify(userData));
    }, []);

    const logout = useCallback(() => {
        setUser(null);
        setAccessToken(null);
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }, []);

    return (
        <AuthContext.Provider value={{
            user,
            accessToken,
            isAuthenticated: !!user && !!accessToken,
            isLoading,
            login,
            logout,
        }}>
            {children}
        </AuthContext.Provider>
    );
};