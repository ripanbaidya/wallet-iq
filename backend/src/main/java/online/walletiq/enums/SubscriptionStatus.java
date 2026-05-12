package online.walletiq.enums;

/**
 * Subscription status for Payment gateway
 */
public enum SubscriptionStatus {

    /**
     * Order created, payment not yet made
     */
    PENDING,

    /**
     * payment verified, subscription live
     */
    ACTIVE,

    /**
     * past expiry date
     */
    EXPIRED,

    /**
     * Payment failed
     */
    FAILED
}
