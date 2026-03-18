package com.walletiq.enums;

/**
 * Represents the type of a financial transaction.
 * Used to distinguish between money received and money spent.
 */
public enum TxnType {

    /**
     * Money received (e.g., salary, bonus)
     */
    INCOME,

    /**
     * Money spent (e.g., food, rent, utilities)
     */
    EXPENSE
}