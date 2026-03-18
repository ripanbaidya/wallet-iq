import { AppError } from '../../errors/AppError';

interface Props {
    error: AppError | null;
    onRetry?: () => void;
}

export function QueryError({ error, onRetry }: Props) {
    if (!error) return null;
    return (
        <div className="flex flex-col items-center gap-3 py-10 text-center text-gray-600">
            <p className="text-sm">{error.message}</p>
            {onRetry && (
                <button
                    onClick={onRetry}
                    className="text-xs text-blue-600 underline hover:text-blue-800"
                >
                    Try again
                </button>
            )}
        </div>
    );
}