package com.walletiq.service;

import com.walletiq.dto.dashboard.DashboardResponse;

import java.time.YearMonth;

public interface DashboardService {

    /**
     * Generates a dashboard snapshot for the given month.
     * <p>Includes:
     * <ul>
     *     <li>Total income, expense, and net balance</li>
     *     <li>Category-wise breakdown for income and expenses</li>
     *     <li>Daily transaction trends</li>
     *     <li>Top 5 expenses</li>
     * </ul>
     * <p>All calculations are scoped to the currently authenticated user.
     *
     * @param month target month
     * @return dashboard response containing aggregated financial insights
     */
    DashboardResponse getDashboard(YearMonth month);
}
