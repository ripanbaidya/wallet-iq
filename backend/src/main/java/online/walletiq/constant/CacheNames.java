package online.walletiq.constant;

/**
 * Central registry of all Redis cache names used in WalletIQ.
 * Always reference these constants — never use raw strings.
 */
public final class CacheNames {

    private CacheNames() {
    }

    public static final String DASHBOARD = "dashboard";
    public static final String CATEGORIES = "categories";
    public static final String PAYMENT_MODES = "payment-modes";
}