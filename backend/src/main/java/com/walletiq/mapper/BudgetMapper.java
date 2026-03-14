package com.walletiq.mapper;

import com.walletiq.dto.budget.BudgetResponse;
import com.walletiq.entity.Budget;

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
