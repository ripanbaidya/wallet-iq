export interface UserProfileResponse {
  id: string;
  fullName: string;
  email: string;
  active: boolean;
  isEmailVerified: boolean;
}

export type Role = 'USER' | 'ADMIN';

export interface UserCountParams {
  role: Role;
  active: boolean;
}

export interface PageInfo {
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PaginatedUsers {
  content: UserProfileResponse[];
  page: PageInfo;
}