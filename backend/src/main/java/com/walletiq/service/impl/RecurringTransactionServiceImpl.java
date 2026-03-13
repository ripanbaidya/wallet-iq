package com.walletiq.service.impl;

import com.walletiq.dto.recurringtransaction.*;
import com.walletiq.entity.*;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.RecurringFrequency;
import com.walletiq.enums.TxnType;
import com.walletiq.exception.RecurringTransactionException;
import com.walletiq.mapper.RecurringTransactionMapper;
import com.walletiq.repository.*;
import com.walletiq.service.CategoryService;
import com.walletiq.service.PaymentModeService;
import com.walletiq.service.RecurringTransactionService;
import com.walletiq.util.SecurityUtils;
import com.walletiq.validator.RecurringTransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final CategoryService categoryService;
    private final PaymentModeService paymentModeService;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringRepository;

    private final RecurringTransactionValidator validator;

    /**
     * Create a new recurring transaction.
     */
    @Override
    @Transactional
    public RecurringTransactionResponse create(RecurringTransactionRequest request) {

        validator.validateDateRange(request.startDate(), request.endDate());

        RecurringTransaction recurring = buildRecurringTransaction(request, getCurrentUser());

        recurringRepository.save(recurring);

        return RecurringTransactionMapper.toResponse(recurring);
    }

    /**
     * Fetch all active recurring transactions.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> getAllByUser() {

        UUID userId = getCurrentUserId();

        return recurringRepository
            .findByUser_IdAndIsActiveTrue(userId)
            .stream()
            .map(RecurringTransactionMapper::toResponse)
            .toList();
    }

    /**
     * Fetch recurring transaction by id
     */
    @Override
    @Transactional(readOnly = true)
    public RecurringTransactionResponse getById(UUID id) {

        RecurringTransaction recurring = findOwnedOrThrow(id, getCurrentUserId());

        return RecurringTransactionMapper.toResponse(recurring);
    }

    /**
     * Update a existing recurring transaction
     */
    @Override
    @Transactional
    public RecurringTransactionResponse update(UUID id, UpdateRecurringTransactionRequest request) {

        RecurringTransaction recurring = findOwnedOrThrow(id, getCurrentUserId());

        updateIfPresent(request.title(), recurring::setTitle);
        updateIfPresent(request.amount(), recurring::setAmount);
        updateIfPresent(request.frequency(), recurring::setFrequency);
        updateIfPresent(request.note(), recurring::setNote);

        if (request.endDate() != null) {
            validator.validateDateRange(recurring.getStartDate(), request.endDate());
            recurring.setEndDate(request.endDate());
        }

        if (request.categoryId() != null) {
            recurring.setCategory(resolveCategory(request.categoryId()));
        }

        if (request.paymentModeId() != null) {
            recurring.setPaymentMode(resolvePaymentMode(request.paymentModeId()));
        }

        recurringRepository.save(recurring);

        return RecurringTransactionMapper.toResponse(recurring);
    }

    /**
     * Deactivate recurring transaction
     */
    @Override
    @Transactional
    public void deactivate(UUID id) {

        RecurringTransaction recurring = findOwnedOrThrow(id, getCurrentUserId());

        if (!recurring.isActive()) {
            throw new RecurringTransactionException(ErrorCode.RECURRING_ALREADY_INACTIVE);
        }

        recurring.setActive(false);

        recurringRepository.save(recurring);

        log.info("Recurring transaction {} deactivated", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ForecastSummaryResponse forecast(int days) {

        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(days);

        List<RecurringTransaction> recurringList = recurringRepository
            .findForecastableByUser(getCurrentUserId(), today, until);

        List<ForecastEntryResponse> entries = new ArrayList<>();
        Totals totals = new Totals();

        for (RecurringTransaction recurring : recurringList) {
            projectRecurring(recurring, until, entries, totals);
        }

        entries.sort(Comparator.comparing(ForecastEntryResponse::projectedDate));

        return ForecastSummaryResponse.builder()
            .forecastDays(days)
            .projectedIncome(totals.income())
            .projectedExpense(totals.expense())
            .projectedNetBalance(totals.net())
            .entries(entries)
            .build();
    }

    @Override
    @Transactional
    public void processDueRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions = recurringRepository
            .findDueTransactions(today);

        log.info("Processing {} due recurring transactions", dueTransactions.size());

        for (RecurringTransaction recurring : dueTransactions) {

            try {
                createTransactionFromRecurring(recurring, today);
                LocalDate nextExecution = computeNextDate(today, recurring.getFrequency());

                if (recurring.getEndDate() != null && nextExecution.isAfter(recurring.getEndDate())) {
                    recurring.setActive(false);
                    log.info("Recurring {} completed", recurring.getId());
                } else {
                    recurring.setNextExecutionDate(nextExecution);
                }

                recurringRepository.save(recurring);
            } catch (Exception ex) {
                log.error("Failed processing recurring {}", recurring.getId(), ex);
            }
        }
    }

    private void projectRecurring(
        RecurringTransaction recurring,
        LocalDate until,
        List<ForecastEntryResponse> entries,
        Totals totals
    ) {

        LocalDate nextDate = recurring.getNextExecutionDate();

        while (!nextDate.isAfter(until)) {
            if (recurring.getEndDate() == null || !nextDate.isAfter(recurring.getEndDate())) {
                entries.add(buildForecastEntry(recurring, nextDate));
                totals.add(recurring.getType(), recurring.getAmount());
            }

            nextDate = computeNextDate(nextDate, recurring.getFrequency());
        }
    }

    private ForecastEntryResponse buildForecastEntry(
        RecurringTransaction recurring,
        LocalDate date
    ) {
        return ForecastEntryResponse.builder()
            .projectedDate(date)
            .title(recurring.getTitle())
            .amount(recurring.getAmount())
            .type(recurring.getType())
            .categoryName(recurring.getCategory() != null
                ? recurring.getCategory().getName()
                : null)
            .build();
    }

    /**
     * Build RecurringTransaction entity from request
     */
    private RecurringTransaction buildRecurringTransaction(
        RecurringTransactionRequest request,
        User user
    ) {

        RecurringTransaction recurring = new RecurringTransaction();

        recurring.setUser(user);
        recurring.setTitle(request.title());
        recurring.setAmount(request.amount());
        recurring.setType(request.type());
        recurring.setFrequency(request.frequency());
        recurring.setStartDate(request.startDate());
        recurring.setEndDate(request.endDate());
        recurring.setNextExecutionDate(request.startDate());
        recurring.setActive(true);
        recurring.setNote(request.note());

        recurring.setCategory(resolveCategory(request.categoryId()));
        recurring.setPaymentMode(resolvePaymentMode(request.paymentModeId()));

        return recurring;
    }

    private void createTransactionFromRecurring(
        RecurringTransaction recurring,
        LocalDate date
    ) {

        Transaction transaction = new Transaction();

        transaction.setUser(recurring.getUser());
        transaction.setAmount(recurring.getAmount());
        transaction.setType(recurring.getType());
        transaction.setDate(date);
        transaction.setCategory(recurring.getCategory());
        transaction.setPaymentMode(recurring.getPaymentMode());

        transaction.setNote("[Auto] " + recurring.getTitle()
            + (recurring.getNote() != null ? " — " + recurring.getNote() : ""));

        transactionRepository.save(transaction);
    }

    // * ------------- Helper methods -------------*/

    private Category resolveCategory(UUID id) {
        return id == null ? null : categoryService.findById(id);
    }

    private PaymentMode resolvePaymentMode(UUID id) {
        return id == null ? null : paymentModeService.findById(id);
    }

    private <T> void updateIfPresent(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private LocalDate computeNextDate(LocalDate from, RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private User getCurrentUser() {
        return userRepository.getReferenceById(getCurrentUserId());
    }

    private RecurringTransaction findOwnedOrThrow(UUID id, UUID userId) {

        return recurringRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> {

                boolean exists = recurringRepository.existsById(id);

                return exists
                    ? new RecurringTransactionException(ErrorCode.RECURRING_ACCESS_DENIED)
                    : new RecurringTransactionException(ErrorCode.RECURRING_NOT_FOUND);
            });
    }

    /**
     * Total Calculator
     */
    private static class Totals {

        private BigDecimal income = BigDecimal.ZERO;
        private BigDecimal expense = BigDecimal.ZERO;

        void add(TxnType type, BigDecimal amount) {
            if (type == TxnType.INCOME) {
                income = income.add(amount);
            } else {
                expense = expense.add(amount);
            }
        }

        BigDecimal income() {
            return income;
        }

        BigDecimal expense() {
            return expense;
        }

        BigDecimal net() {
            return income.subtract(expense);
        }
    }
}