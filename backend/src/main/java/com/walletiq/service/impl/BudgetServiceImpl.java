package com.walletiq.service.impl;

import com.walletiq.dto.budget.BudgetRequest;
import com.walletiq.dto.budget.BudgetResponse;
import com.walletiq.dto.budget.BudgetStatusResponse;
import com.walletiq.entity.Budget;
import com.walletiq.entity.Category;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.BudgetException;
import com.walletiq.mapper.BudgetMapper;
import com.walletiq.repository.BudgetRepository;
import com.walletiq.repository.TransactionRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.BudgetService;
import com.walletiq.service.CategoryService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    private final CategoryService categoryService;

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        User user = currentUser();

        Category category = categoryService.findById(request.categoryId());

        // Prevent duplicate budget for same category + month
        if (budgetRepository.existsByUser_IdAndCategoryIdAndMonth(
            user.getId(), request.categoryId(), request.month())
        ) {
            throw new BudgetException(ErrorCode.BUDGET_DUPLICATE);
        }

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonth(request.month());
        budget.setLimitAmount(request.limitAmount());
        budget.setAlertThreshold(request.alertThreshold());

        budgetRepository.saveAndFlush(budget);

        return BudgetMapper.toResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getByMonth(YearMonth month) {
        return budgetRepository.findByUser_IdAndMonth(currentUserId(), month)
            .stream()
            .map(BudgetMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetStatusResponse getStatus(UUID budgetId) {
        UUID userId = currentUserId();

        Budget budget = findOwnedOrThrow(budgetId, userId);

        BigDecimal spent = transactionRepository.sumExpensesByCategoryAndMonth(
            userId,
            budget.getCategory().getId(),
            budget.getMonth().atDay(1),
            budget.getMonth().atEndOfMonth()
        );

        BigDecimal limit = budget.getLimitAmount();
        BigDecimal remaining = limit.subtract(spent);
        double usagePct = spent.divide(limit, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();

        return BudgetStatusResponse.builder()
            .budgetId(budget.getId())
            .categoryName(budget.getCategory().getName())
            .month(budget.getMonth())
            .limitAmount(limit)
            .spentAmount(spent)
            .remainingAmount(remaining)
            .usagePercentage(usagePct)
            .thresholdBreached(usagePct >= budget.getAlertThreshold())
            .limitBreached(spent.compareTo(limit) > 0)
            .build();
    }

    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    private UUID currentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * Retrieves a budget owned by the given user.
     * <p>If the budget exists but is owned by another user, an access denied
     * exception is thrown. If it does not exist, a not found exception is thrown.
     *
     * @param id     budget identifier
     * @param userId current user identifier
     * @return budget entity
     * @throws BudgetException if budget is not found or access is denied
     */
    private Budget findOwnedOrThrow(UUID id, UUID userId) {
        return budgetRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> {
                boolean exists = budgetRepository.existsById(id);
                return exists
                    ? new BudgetException(ErrorCode.BUDGET_ACCESS_DENIED)
                    : new BudgetException(ErrorCode.BUDGET_NOT_FOUND);
            });
    }
}