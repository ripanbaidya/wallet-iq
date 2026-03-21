import type { TxnType } from "../../types/transaction.types";

export interface FilterState {
  type: TxnType | "";
  dateFrom: string;
  dateTo: string;
}

interface Props {
  filters: FilterState;
  onChange: (filters: FilterState) => void;
  onReset: () => void;
}

export const DEFAULT_FILTERS: FilterState = {
  type: "",
  dateFrom: "",
  dateTo: "",
};

const TransactionFilters: React.FC<Props> = ({
  filters,
  onChange,
  onReset,
}) => {
  const hasActiveFilters =
    filters.type !== "" || filters.dateFrom !== "" || filters.dateTo !== "";

  return (
    // flex-wrap ensures wrapping on small screens
    <div className="flex flex-wrap items-end gap-3 bg-white border border-gray-200 rounded-lg p-4">
      {/* Type */}
      <div className="flex flex-col gap-1 min-w-[120px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">Type</label>

        <select
          value={filters.type}
          onChange={(e) =>
            onChange({ ...filters, type: e.target.value as TxnType | "" })
          }
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black bg-white
            w-full sm:min-w-[130px]   /* full width on mobile */
          "
        >
          <option value="">All types</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
      </div>

      {/* Date From */}
      <div className="flex flex-col gap-1 min-w-[140px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">From</label>

        <input
          type="date"
          value={filters.dateFrom}
          onChange={(e) => onChange({ ...filters, dateFrom: e.target.value })}
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black
            w-full
          "
        />
      </div>

      {/* Date To */}
      <div className="flex flex-col gap-1 min-w-[140px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">To</label>

        <input
          type="date"
          value={filters.dateTo}
          onChange={(e) => onChange({ ...filters, dateTo: e.target.value })}
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black
            w-full
          "
        />
      </div>

      {/* Reset */}
      {hasActiveFilters && (
        <button
          onClick={onReset}
          className="
            px-3 py-2 text-sm text-gray-500 border border-gray-200 rounded-lg
            hover:bg-gray-50 transition-colors
            w-full sm:w-auto        /* full width on mobile */
            flex-shrink-0
          "
        >
          Reset
        </button>
      )}
    </div>
  );
};

export default TransactionFilters;
