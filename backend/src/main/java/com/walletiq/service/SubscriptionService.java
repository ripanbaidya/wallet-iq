package com.walletiq.service;

import com.walletiq.dto.subscription.CreateOrderResponse;
import com.walletiq.dto.subscription.SubscriptionStatusResponse;
import com.walletiq.dto.subscription.VerifyPaymentRequest;

import java.util.UUID;

/**
 * Service interface for managing user subscriptions via Razorpay.
 * <p>
 * Covers the full subscription lifecycle: order creation, payment verification,
 * status querying, access gating, and scheduled expiry processing.
 */
public interface SubscriptionService {

    /**
     * Creates a Razorpay order for the authenticated user and persists a
     * {@code PENDING} subscription record.
     *
     * @return order details (order ID, amount, currency) required by the
     * frontend to initialise the Razorpay payment modal
     */
    CreateOrderResponse createOrder();

    /**
     * Verifies the HMAC signature returned by Razorpay after a payment attempt.
     * On successful verification, transitions the associated subscription to {@code ACTIVE}.
     *
     * @param request contains the Razorpay order ID, payment ID, and signature
     * @throws com.walletiq.exception.SubscriptionException if the signature is invalid
     *                                                      or the corresponding subscription record cannot be found
     */
    void verifyPayment(VerifyPaymentRequest request);

    /**
     * Returns the current subscription status for the authenticated user,
     * including plan details and expiry information.
     *
     * @return the user's subscription status response
     */
    SubscriptionStatusResponse getStatus();

    /**
     * Checks whether the given user currently holds an active, non-expired subscription.
     * Used internally by {@code ChatService} to gate access to premium features.
     *
     * @param userId the ID of the user to check
     * @return {@code true} if an active subscription exists, {@code false} otherwise
     */
    boolean hasActiveSubscription(UUID userId);

    /**
     * Scans for subscriptions that are still marked {@code ACTIVE} but have passed
     * their expiry timestamp, and transitions them to {@code EXPIRED}.
     * Intended to be invoked exclusively by the subscription expiry scheduler.
     */
    void markExpiredSubscriptions();
}