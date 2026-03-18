export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  fullName: string;
  email: string;
  password: string;
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

export interface AuthResponse {
  user: AuthUserResponse;
  tokens: TokenResponse;
}

// What we store in context
export interface AuthUser {
  userId: string;
  fullName: string;
  email: string;
}