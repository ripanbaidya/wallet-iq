package com.walletiq.service;

import com.walletiq.dto.budget.BudgetRequest;
import com.walletiq.dto.budget.BudgetResponse;
import com.walletiq.dto.budget.BudgetStatusResponse;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface BudgetService {

    BudgetResponse create(BudgetRequest request);

    List<BudgetResponse> getByMonth(YearMonth month);

    BudgetStatusResponse getStatus(UUID budgetId);
}
