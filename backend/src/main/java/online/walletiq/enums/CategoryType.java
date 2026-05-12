package online.walletiq.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines the type of financial category.
 * Used to differentiate whether a category belongs to income or expense.
 * The UI can use this to show only relevant categories when creating a transaction.
 */
@Schema(description = "Type of financial category")
public enum CategoryType {

    /** Category used for income transactions (e.g., salary, bonus) */
    INCOME,

    /** Category used for expense transactions (e.g., food, rent) */
    EXPENSE
}