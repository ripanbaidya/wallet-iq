package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for budget-related failures.
 * <p>This exception represents errors that occur during budget operations,
 * such as creating, updating, retrieving, or enforcing budget constraints.
 */
public class BudgetException extends BusinessException {

    public BudgetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
