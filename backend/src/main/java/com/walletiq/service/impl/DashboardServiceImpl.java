package com.walletiq.service.impl;

import com.walletiq.constant.CacheNames;
import com.walletiq.dto.dashboard.*;
import com.walletiq.enums.TxnType;
import com.walletiq.mapper.TransactionMapper;
import com.walletiq.repository.TransactionRepository;
import com.walletiq.service.DashboardService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link DashboardService}.
 * <p>Aggregates transaction data to provide a monthly dashboard including summary
 * metrics, category breakdowns, daily trends, and top expenses for the current user.
 * <p>Results are cached to improve performance for repeated queries.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter MONTH_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.DASHBOARD, keyGenerator = "dashboardKeyGenerator")
    public DashboardResponse getDashboard(YearMonth month) {

        UUID userId = SecurityUtils.getCurrentUserId();
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        // Calculate summary - income, expense and netBalance
        BigDecimal totalIncome = transactionRepository
            .sumByUserAndTypeAndDateBetween(userId, TxnType.INCOME, from, to);
        BigDecimal totalExpense = transactionRepository
            .sumByUserAndTypeAndDateBetween(userId, TxnType.EXPENSE, from, to);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // Build the summary
        DashboardSummary summary = new DashboardSummary(totalIncome, totalExpense, netBalance);

        // Calculate category-wise breakdowns -
        List<CategoryBreakdownResponse> expenseByCategory = TransactionMapper.toBreakdownResponse(
            transactionRepository.findCategoryBreakdown(userId, TxnType.EXPENSE, from, to),
            totalExpense
        );
        List<CategoryBreakdownResponse> incomeByCategory = TransactionMapper.toBreakdownResponse(
            transactionRepository.findCategoryBreakdown(userId, TxnType.INCOME, from, to),
            totalIncome
        );

        List<DailyTrendItem> dailyTrend = transactionRepository
            .findDailyTrend(userId, from, to);

        List<TopExpenseItem> topExpenses = transactionRepository
            .findTop5Expenses(userId, from, to);

        return new DashboardResponse(
            month.format(MONTH_FMT),
            summary,
            expenseByCategory,
            incomeByCategory,
            dailyTrend,
            topExpenses
        );
    }
}