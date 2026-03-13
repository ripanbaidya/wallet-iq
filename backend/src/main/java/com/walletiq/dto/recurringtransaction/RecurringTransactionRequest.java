package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.RecurringFrequency;
import com.walletiq.enums.TxnType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecurringTransactionRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    BigDecimal amount,

    @NotNull(message = "Transaction type is required")
    TxnType type,

    @NotNull(message = "Frequency is required")
    RecurringFrequency frequency,

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    LocalDate startDate,

    LocalDate endDate,

    String note,

    UUID categoryId,

    UUID paymentModeId

) {
}