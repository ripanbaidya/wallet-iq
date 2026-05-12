package online.walletiq.service;

import online.walletiq.dto.recurringtransaction.ForecastSummaryResponse;
import online.walletiq.dto.recurringtransaction.RecurringTransactionRequest;
import online.walletiq.dto.recurringtransaction.RecurringTransactionResponse;
import online.walletiq.dto.recurringtransaction.UpdateRecurringTransactionRequest;
import online.walletiq.exception.RecurringTransactionException;

import java.util.List;
import java.util.UUID;

public interface RecurringTransactionService {

    /**
     * Creates a new recurring transaction for the current user.
     * <p>Validates the date range and initializes the next execution date
     * based on the start date.
     *
     * @param request recurring transaction request
     * @return created recurring transaction response
     * @throws RecurringTransactionException if date range is invalid
     */
    RecurringTransactionResponse create(RecurringTransactionRequest request);

    /**
     * Retrieves all active recurring transactions for the current user.
     *
     * @return list of recurring transaction responses
     */
    List<RecurringTransactionResponse> getAllByUser();

    /**
     * Retrieves a recurring transaction by ID for the current user.
     *
     * @param id recurring transaction identifier
     * @return recurring transaction response
     * @throws RecurringTransactionException if not found or access is denied
     */
    RecurringTransactionResponse getById(UUID id);

    /**
     * Updates an existing recurring transaction.
     *
     * <p>Supports partial updates. Validates date range when end date is modified.
     *
     * @param id      recurring transaction identifier
     * @param request update request
     * @return updated recurring transaction response
     * @throws RecurringTransactionException if not found or access is denied
     */
    RecurringTransactionResponse update(UUID id, UpdateRecurringTransactionRequest request);

    /**
     * Deactivates a recurring transaction.
     * <p>Once deactivated, it will no longer generate future transactions.
     *
     * @param id recurring transaction identifier
     * @throws RecurringTransactionException if not found, access is denied,
     *                                       or already inactive
     */
    void deactivate(UUID id);

    /**
     * Generates a financial forecast based on recurring transactions.
     * <p>Calculates projected income, expenses, and net balance over
     * the specified number of days.
     * <p>Only considers active recurring transactions within the given window.
     *
     * @param days number of days to forecast
     * @return forecast summary response including projected entries and totals
     */
    ForecastSummaryResponse forecast(int days);

    /**
     * Processes all due recurring transactions for the current date.
     * <p>For each due recurring transaction:
     * <ul>
     *     <li>Creates a corresponding transaction</li>
     *     <li>Updates the next execution date</li>
     *     <li>Deactivates the recurring if end date is reached</li>
     * </ul>
     *
     * <p>Failures are logged and do not interrupt processing of other records.
     */
    void processDueRecurringTransactions();
}
