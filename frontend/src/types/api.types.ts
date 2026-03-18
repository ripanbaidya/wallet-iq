/**
 * Response for all Success API
 */
export interface ResponseWrapper<T> {
    success: true;
    status: number;
    message: string;
    data: T;
    timestamp: string;
}

/**
 * Response for all Error API
 */
export interface ErrorResponse {
    success: false;
    error: ErrorDetail;
}

export interface ErrorDetail {
    type: ErrorType;
    code: string;
    message: string;
    status: number;
    timestamp: string;
    path: string;
    errors?: FieldError[];
}

export interface FieldError {
    field: string;
    message: string;
}

export type ErrorType =
    | 'VALIDATION'
    | 'AUTHENTICATION'
    | 'AUTHORIZATION'
    | 'NOT_FOUND'
    | 'CONFLICT'
    | 'BUSINESS'
    | 'INTERNAL'
    | 'SERVICE_UNAVAILABLE';

