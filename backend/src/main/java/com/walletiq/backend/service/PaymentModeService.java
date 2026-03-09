package com.walletiq.backend.service;

import com.walletiq.backend.dto.paymentmode.CreatePaymentModeRequest;
import com.walletiq.backend.dto.paymentmode.PaymentModeResponse;
import com.walletiq.backend.dto.paymentmode.UpdatePaymentModeRequest;
import com.walletiq.backend.exception.PaymentModeException;

import java.util.List;
import java.util.UUID;

public interface PaymentModeService {

    /**
     * Returns system default payment modes + the current user's own payment modes.
     */
    List<PaymentModeResponse> getAllPaymentModes();

    /**
     * Creates a new payment mode owned by the current user.
     *
     * @throws PaymentModeException if a payment mode with the same name already exists for this user.
     */
    PaymentModeResponse createPaymentMode(CreatePaymentModeRequest request);

    /**
     * Updates a payment mode owned by the current user.
     *
     * @throws PaymentModeException if not found, not owned by user, or name already taken.
     */
    PaymentModeResponse updatePaymentMode(UUID id, UpdatePaymentModeRequest request);

    /**
     * Deletes a payment mode owned by the current user.
     *
     * @throws PaymentModeException if not found or not owned by user.
     */
    void deletePaymentMode(UUID id);
}