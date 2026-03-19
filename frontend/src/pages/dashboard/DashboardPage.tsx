import { useState } from 'react';
import { dashboardService } from '../../services/dashboardService';
import { useAppQuery } from '../../hooks/useAppQuery';
import { QueryError } from '../../components/ui/QueryError';
import Spinner from '../../components/ui/Spinner';

import MonthNavigator from '../../components/MonthNavigator';
import SummaryCards from '../../components/SummaryCards';
import DailyTrendChart from '../../components/DailyTrendChart';
import CategoryBreakdown from '../../components/CategoryBreakdown';
import TopExpenses from '../../components/TopExpenses';

// ─── Helpers ──────────────────────────────────────────────────────────────────

const currentMonth = () => {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
};

// ─── Page ─────────────────────────────────────────────────────────────────────

export default function DashboardPage() {
    const [month, setMonth] = useState(currentMonth);

    const { data, isLoading, error, refetch, isFetching } = useAppQuery({
        queryKey: ['dashboard', month],
        queryFn: () => dashboardService.getDashboardData(month),
        // Keep previous month data visible while fetching next month
        placeholderData: (prev) => prev,
    });

    const dashboard = data?.data;

    // Subtle re-fetch indicator (e.g. when switching months)
    const isRefetching = isFetching && !isLoading;

    return (
        <div className="max-w-6xl mx-auto space-y-6">

            {/* ── Header ── */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <div>
                    <h1 className="text-xl font-semibold text-gray-900">Dashboard</h1>
                    <p className="text-sm text-gray-500 mt-0.5">
                        Your financial overview.
                    </p>
                </div>
                <MonthNavigator month={month} onChange={setMonth} />
            </div>

            {/* ── Loading (initial) ── */}
            {isLoading ? (
                <div className="flex justify-center py-24">
                    <Spinner />
                </div>
            ) : error ? (
                <QueryError error={error} onRetry={refetch} />
            ) : !dashboard ? null : (
                <div className={`space-y-6 transition-opacity duration-150 ${isRefetching ? 'opacity-60' : 'opacity-100'}`}>

                    {/* ── Summary Cards ── */}
                    <SummaryCards summary={dashboard.summary} />

                    {/* ── Daily Trend ── */}
                    <div className="bg-white border border-gray-200 rounded-lg p-5">
                        <h2 className="text-sm font-medium text-gray-700 mb-4">
                            Daily Trend
                        </h2>
                        <DailyTrendChart data={dashboard.dailyTrend} />
                    </div>

                    {/* ── Category Breakdowns ── */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                        <div className="bg-white border border-gray-200 rounded-lg p-5">
                            <CategoryBreakdown
                                title="Expense by Category"
                                data={dashboard.expenseByCategory}
                                type="expense"
                            />
                        </div>
                        <div className="bg-white border border-gray-200 rounded-lg p-5">
                            <CategoryBreakdown
                                title="Income by Category"
                                data={dashboard.incomeByCategory}
                                type="income"
                            />
                        </div>
                    </div>

                    {/* ── Top Expenses ── */}
                    <div className="bg-white border border-gray-200 rounded-lg p-5">
                        <h2 className="text-sm font-medium text-gray-700 mb-4">
                            Top 5 Expenses
                        </h2>
                        <TopExpenses data={dashboard.topExpenses} />
                    </div>

                </div>
            )}
        </div>
    );
}