import type {ErrorDetail, ErrorType, FieldError} from '../types/api.types';

export class AppError extends Error {
    readonly type: ErrorType;
    readonly code: string;
    readonly status: number;
    readonly path: string;
    readonly timestamp: string;
    readonly fieldErrors: FieldError[];

    constructor(detail: ErrorDetail) {
        super(detail.message);
        this.name = 'AppError';
        this.type = detail.type;
        this.code = detail.code;
        this.status = detail.status;
        this.path = detail.path;
        this.timestamp = detail.timestamp;
        this.fieldErrors = detail.errors ?? [];
    }

    // Convenience predicates
    get isValidation() {
        return this.type === 'VALIDATION';
    }

    get isAuthentication() {
        return this.type === 'AUTHENTICATION';
    }

    get isAuthorization() {
        return this.type === 'AUTHORIZATION';
    }

    get isNotFound() {
        return this.type === 'NOT_FOUND';
    }

    get isConflict() {
        return this.type === 'CONFLICT';
    }

    get isBusiness() {
        return this.type === 'BUSINESS';
    }

    get isServerError() {
        return this.type === 'INTERNAL' || this.type === 'SERVICE_UNAVAILABLE';
    }

    // Get error for a specific field (for forms)
    getFieldError(field: string): string | undefined {
        return this.fieldErrors.find(e => e.field === field)?.message;
    }

    // Convert field errors to a map: { email: 'must not be blank', ... }
    toFieldErrorMap(): Record<string, string> {
        return Object.fromEntries(this.fieldErrors.map(e => [e.field, e.message]));
    }
}