package online.walletiq.service;

import online.walletiq.entity.Budget;
import online.walletiq.entity.SavingsGoal;
import online.walletiq.repository.BudgetRepository;
import online.walletiq.repository.SavingsGoalRepository;
import online.walletiq.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * This service fetches budgets and goals for the user and formats
 * them as readable text blocks for the LLM
 */
@Service
@RequiredArgsConstructor
public class FinancialContextService {

    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Builds a structured text block describing the user's current
     * budgets and their real-time spend status.
     */
    @Transactional(readOnly = true)
    public String buildBudgetContext(UUID userId) {
        YearMonth currentMonth = YearMonth.now();

        List<Budget> budgets = budgetRepository.findByUser_IdAndMonth(userId, currentMonth);

        if (budgets.isEmpty()) return "No budgets set for the current month.";

        StringBuilder sb = new StringBuilder();
        sb.append("=== BUDGET STATUS (").append(currentMonth).append(") ===\n");

        for (Budget b : budgets) {
            BigDecimal spent = transactionRepository.sumExpensesByCategoryAndMonth(
                userId,
                b.getCategory().getId(),
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth()
            );

            BigDecimal limit = b.getLimitAmount();
            BigDecimal remaining = limit.subtract(spent).max(BigDecimal.ZERO);
            double pct = spent.divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

            sb.append(String.format(
                "- %s: spent ₹%.2f of ₹%.2f limit (%.1f%% used, ₹%.2f remaining)%s\n",
                b.getCategory().getName(),
                spent, limit, pct, remaining,
                pct > 100 ? " ⚠️ EXCEEDED" : (pct >= b.getAlertThreshold() ? " ⚠️ NEAR LIMIT" : "")
            ));
        }

        return sb.toString();
    }

    /**
     * Builds a structured text block describing all the user's savings goals
     * and their current progress.
     */
    @Transactional(readOnly = true)
    public String buildGoalContext(UUID userId) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUser_Id(userId);

        if (goals.isEmpty()) return "No savings goals set.";

        StringBuilder sb = new StringBuilder();
        sb.append("=== SAVINGS GOALS ===\n");

        for (SavingsGoal g : goals) {
            BigDecimal saved = g.getSavedAmount();
            BigDecimal target = g.getTargetAmount();
            BigDecimal remaining = target.subtract(saved).max(BigDecimal.ZERO);
            double pct = saved.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
            long daysLeft = java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), g.getDeadline());

            sb.append(String.format(
                "- %s: saved ₹%.2f of ₹%.2f (%.1f%%) | status: %s | deadline: %s (%d days %s)\n",
                g.getTitle(),
                saved, target, pct,
                g.getStatus(),
                g.getDeadline(),
                Math.abs(daysLeft),
                daysLeft >= 0 ? "remaining" : "overdue"
            ));
        }

        return sb.toString();
    }
}