package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.RecurringFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateRecurringTransactionRequest(

    @Size(max = 100)
    String title,

    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    BigDecimal amount,

    RecurringFrequency frequency,

    LocalDate endDate,

    String note,

    UUID categoryId,

    UUID paymentModeId

) {
}