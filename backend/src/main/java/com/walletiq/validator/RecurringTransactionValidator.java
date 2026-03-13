package com.walletiq.validator;

import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.RecurringTransactionException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RecurringTransactionValidator {

    public void validateDateRange(LocalDate start, LocalDate end) {
        if (end != null && !end.isAfter(start)) {
            throw new RecurringTransactionException(ErrorCode.RECURRING_END_DATE_BEFORE_START);
        }
    }
}
