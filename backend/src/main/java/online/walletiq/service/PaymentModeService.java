package online.walletiq.service;

import online.walletiq.dto.paymentmode.CreatePaymentModeRequest;
import online.walletiq.dto.paymentmode.PaymentModeResponse;
import online.walletiq.dto.paymentmode.UpdatePaymentModeRequest;
import online.walletiq.entity.PaymentMode;
import online.walletiq.exception.PaymentModeException;

import java.util.List;
import java.util.UUID;

public interface PaymentModeService {

    /**
     * Retrieves all payment modes visible to the current user.
     * <p>Includes both system default and user-defined payment modes.
     *
     * @return list of payment mode responses
     */
    List<PaymentModeResponse> getAll();

    /**
     * Creates a new payment mode for the current user.
     * <p>Ensures no duplicate payment mode exists with the same name
     * (case-insensitive) for the user.
     *
     * @param request payment mode creation request
     * @return created payment mode response
     * @throws PaymentModeException if a payment mode with the same name already exists
     */
    PaymentModeResponse create(CreatePaymentModeRequest request);

    /**
     * Updates an existing payment mode owned by the current user.
     * <p>Validates uniqueness only when the name is changed.
     *
     * @param id      payment mode identifier
     * @param request update request
     * @return updated payment mode response
     * @throws PaymentModeException if payment mode is not found, not owned by user,
     *                              or a duplicate name exists
     */
    PaymentModeResponse update(UUID id, UpdatePaymentModeRequest request);

    /**
     * Deletes a payment mode owned by the current user.
     *
     * @param id payment mode identifier
     * @throws PaymentModeException if payment mode is not found or not owned by user
     */
    void delete(UUID id);

    /**
     * Retrieves a payment mode by its ID.
     *
     * @param id payment mode identifier
     * @return payment mode entity
     * @throws PaymentModeException if payment mode is not found
     */
    PaymentMode findById(UUID id);
}