export interface LoginRequest {
    email: string;
    password: string;
}

export interface SignupRequest {
    fullName: string;
    email: string;
    password: string;
}

export interface LogoutRequest {
    refreshToken: string;
}

export interface AuthResponse {
    user: AuthUserResponse;
    tokens: TokenResponse;
}

export interface TokenResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
}

export interface AuthUserResponse {
    id: string;
    fullName: string;
    email: string;
}

export interface SendOtpRequest {
    email: string;
}

export interface VerifyOtpRequest {
    email: string;
    otp: string;
}

export interface OtpResponse {
    message: string;
}