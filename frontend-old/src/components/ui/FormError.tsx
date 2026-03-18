interface Props { error: string | null | undefined; }

export function FormError({ error }: Props) {
    if (!error) return null;
    return (
        <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
        </div>
    );
}