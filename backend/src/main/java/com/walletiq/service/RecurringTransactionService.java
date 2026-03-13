package com.walletiq.service;

import com.walletiq.dto.recurringtransaction.ForecastSummaryResponse;
import com.walletiq.dto.recurringtransaction.RecurringTransactionRequest;
import com.walletiq.dto.recurringtransaction.RecurringTransactionResponse;
import com.walletiq.dto.recurringtransaction.UpdateRecurringTransactionRequest;

import java.util.List;
import java.util.UUID;

public interface RecurringTransactionService {

    RecurringTransactionResponse create(RecurringTransactionRequest request);

    List<RecurringTransactionResponse> getAllByUser();

    RecurringTransactionResponse getById(UUID id);

    RecurringTransactionResponse update(UUID id, UpdateRecurringTransactionRequest request);

    void deactivate(UUID id);

    ForecastSummaryResponse forecast(int days);

    void processDueRecurringTransactions();
}
