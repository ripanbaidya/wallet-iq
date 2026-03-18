import { useMutation, UseMutationOptions, UseMutationResult } from '@tanstack/react-query';
import { AppError } from '../errors/AppError';

export function useAppMutation<TData, TVariables>(
    options: UseMutationOptions<TData, AppError, TVariables>
): UseMutationResult<TData, AppError, TVariables> {
    return useMutation<TData, AppError, TVariables>(options);
}