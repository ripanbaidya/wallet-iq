package online.walletiq.service;

import online.walletiq.dto.budget.BudgetRequest;
import online.walletiq.dto.budget.BudgetResponse;
import online.walletiq.dto.budget.BudgetStatusResponse;
import online.walletiq.exception.BudgetException;
import online.walletiq.exception.CategoryException;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface BudgetService {

    /**
     * Creates a new budget for the current user.
     * <p>Ensures that only one budget exists per category per month.
     *
     * @param request budget creation request
     * @return created budget response
     * @throws BudgetException   if a budget already exists for the same category and month
     * @throws CategoryException if the category does not exist
     */
    BudgetResponse create(BudgetRequest request);

    /**
     * Retrieves all budgets for the current user for a given month.
     *
     * @param month target month
     * @return list of budget responses
     */
    List<BudgetResponse> getByMonth(YearMonth month);

    /**
     * Retrieves the current status of a budget.
     * <p>Calculates total spent amount, remaining budget, usage percentage,
     * and determines whether alert or limit thresholds are breached.
     *
     * @param budgetId budget identifier
     * @return budget status response
     * @throws BudgetException if the budget is not found or access is denied
     */
    BudgetStatusResponse getStatus(UUID budgetId);
}
