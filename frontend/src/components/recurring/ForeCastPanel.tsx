import { useState } from "react";
import { recurringService } from "../../services/recurringService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { QueryError } from "../ui/QueryError";
import Spinner from "../ui/Spinner";
import type { ForecastEntryResponse } from "../../types/recurring.types";

// ── Helpers ───────────────────────────────────────────────────────────────────

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
  });

// ── Forecast entry row ────────────────────────────────────────────────────────

const ForecastEntry: React.FC<{
  entry: ForecastEntryResponse;
  idx: number;
}> = ({ entry, idx }) => {
  const isIncome = entry.type === "INCOME";
  return (
    <tr
      className={`border-t border-gray-100 ${
        idx % 2 === 0 ? "bg-white" : "bg-gray-50/50"
      }`}
    >
      <td className="px-4 py-2.5 text-xs text-gray-500 whitespace-nowrap">
        {formatDate(entry.projectedDate)}
      </td>
      <td className="px-4 py-2.5 text-sm text-gray-800 font-medium">
        {entry.title}
      </td>
      <td className="px-4 py-2.5 text-xs text-gray-500">
        {entry.categoryName ?? <span className="text-gray-300">—</span>}
      </td>
      <td className="px-4 py-2.5 text-right whitespace-nowrap">
        <span
          className={`text-sm font-medium ${
            isIncome ? "text-green-600" : "text-red-600"
          }`}
        >
          {isIncome ? "+" : "-"}
          {fmt(entry.amount)}
        </span>
      </td>
    </tr>
  );
};

// ── Main component ────────────────────────────────────────────────────────────

const FORECAST_OPTIONS = [7, 30, 90, 180, 365] as const;

const ForecastPanel: React.FC = () => {
  const [days, setDays] = useState<number>(30);

  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["recurring-forecast", days],
    queryFn: () => recurringService.getForecast(days),
  });

  const forecast = data?.data;

  return (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
      {/* Header */}
      <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
        <h2 className="text-sm font-semibold text-gray-900">
          Cash Flow Forecast
        </h2>

        {/* Days selector */}
        <div className="flex gap-1">
          {FORECAST_OPTIONS.map((d) => (
            <button
              key={d}
              onClick={() => setDays(d)}
              className={`px-2.5 py-1 text-xs rounded-lg font-medium transition-colors ${
                days === d
                  ? "bg-gray-900 text-white"
                  : "text-gray-500 hover:bg-gray-100"
              }`}
            >
              {d}d
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : error ? (
        <div className="p-6">
          <QueryError error={error} onRetry={refetch} />
        </div>
      ) : !forecast ? null : (
        <>
          {/* Summary cards */}
          <div className="grid grid-cols-3 gap-px bg-gray-100 border-b border-gray-100">
            <div className="bg-white px-5 py-4">
              <p className="text-xs text-gray-400 mb-1">Projected Income</p>
              <p className="text-lg font-semibold text-green-600">
                {fmt(forecast.projectedIncome)}
              </p>
            </div>
            <div className="bg-white px-5 py-4">
              <p className="text-xs text-gray-400 mb-1">Projected Expenses</p>
              <p className="text-lg font-semibold text-red-600">
                {fmt(forecast.projectedExpense)}
              </p>
            </div>
            <div className="bg-white px-5 py-4">
              <p className="text-xs text-gray-400 mb-1">Net Balance</p>
              <p
                className={`text-lg font-semibold ${
                  forecast.projectedNetBalance >= 0
                    ? "text-gray-900"
                    : "text-red-600"
                }`}
              >
                {fmt(forecast.projectedNetBalance)}
              </p>
            </div>
          </div>

          {/* Entries table */}
          {forecast.entries.length === 0 ? (
            <div className="py-10 text-center text-sm text-gray-400">
              No scheduled transactions in the next {days} days.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="text-left text-xs text-gray-400 bg-gray-50 border-b border-gray-100">
                    <th className="px-4 py-2.5 font-medium">Date</th>
                    <th className="px-4 py-2.5 font-medium">Title</th>
                    <th className="px-4 py-2.5 font-medium">Category</th>
                    <th className="px-4 py-2.5 font-medium text-right">
                      Amount
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {forecast.entries.map((entry, idx) => (
                    <ForecastEntry
                      key={`${entry.projectedDate}-${entry.title}-${idx}`}
                      entry={entry}
                      idx={idx}
                    />
                  ))}
                </tbody>
                <tfoot>
                  <tr className="border-t-2 border-gray-200 bg-gray-50">
                    <td
                      colSpan={3}
                      className="px-4 py-3 text-xs text-gray-500 font-medium"
                    >
                      {forecast.entries.length} transaction
                      {forecast.entries.length !== 1 ? "s" : ""} over {days}{" "}
                      days
                    </td>
                    <td className="px-4 py-3 text-right">
                      <span
                        className={`text-sm font-bold ${
                          forecast.projectedNetBalance >= 0
                            ? "text-gray-900"
                            : "text-red-600"
                        }`}
                      >
                        {fmt(forecast.projectedNetBalance)}
                      </span>
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ForecastPanel;
