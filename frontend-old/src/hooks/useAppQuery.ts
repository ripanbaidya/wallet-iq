import { useQuery, type UseQueryOptions, type UseQueryResult } from '@tanstack/react-query';
import { AppError } from '../errors/AppError';

export function useAppQuery<TData>(
    options: UseQueryOptions<TData, AppError>
): UseQueryResult<TData, AppError> {
    return useQuery<TData, AppError>({
        retry: (failureCount, error) => {
            // Don't retry on auth/client errors — only server errors
            if (error instanceof AppError && !error.isServerError) return false;
            return failureCount < 2;
        },
        ...options,
    });
}