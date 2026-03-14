package com.walletiq.service;

import com.walletiq.entity.Budget;
import com.walletiq.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetAlertService {

    private final BudgetRepository budgetRepository;

    /**
     * Called after any EXPENSE transaction is created or updated.
     * Checks if the user has a budget for this category + month and logs
     * a warning if threshold or limit is breached.
     * Intentionally does NOT throw — budget alerts must never block a
     * transaction from being saved.
     */
    public void checkAndAlert(UUID userId, UUID categoryId, YearMonth month) {
        log.debug("Checking if transaction exists for user {} and category {}", userId, categoryId);

        if (categoryId == null) return;

        Optional<Budget> budgetOpt = budgetRepository
            .findByUser_IdAndCategoryIdAndMonth(userId, categoryId, month);

        // No budget set for this category, nothing to check
        if (budgetOpt.isEmpty()) return;

        Budget budget = budgetOpt.get();

        BigDecimal spent = budgetRepository.sumExpensesByCategoryAndMonth(
            userId, categoryId, month.toString()
        );

        BigDecimal limit = budget.getLimitAmount();
        double usagePct = spent.divide(limit, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();

        log.debug("Budget check: userId: {} category: {} month: {} spent: {} limit: {} usage: {}%",
            userId, categoryId, month, spent, limit, String.format("%.1f", usagePct));

        if (spent.compareTo(limit) > 0) {
            log.warn("[BUDGET EXCEEDED] userId: {} category: {} month: {} spent: {} limit: {}",
                userId, categoryId, month, spent, limit);
            // TODO: plug in email/push notification here later
        } else if (usagePct >= budget.getAlertThreshold()) {
            log.warn("[BUDGET THRESHOLD] userId: {} category: {} month: {} usage: {}% threshold: {}%",
                userId, categoryId, month, String.format("%.1f", usagePct),
                budget.getAlertThreshold());
            // TODO: plug in email/push notification here later
        }
    }
}