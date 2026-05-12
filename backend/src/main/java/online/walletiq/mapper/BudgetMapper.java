package online.walletiq.mapper;

import online.walletiq.dto.budget.BudgetResponse;
import online.walletiq.entity.Budget;

public final class BudgetMapper {

    private BudgetMapper() {
    }

    public static BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
            budget.getId(),
            budget.getCategory().getName(),
            budget.getCategory().getId(),
            budget.getMonth(),
            budget.getLimitAmount(),
            budget.getAlertThreshold()
        );
    }
}
