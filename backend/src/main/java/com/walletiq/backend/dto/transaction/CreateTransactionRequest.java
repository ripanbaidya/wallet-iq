package com.walletiq.backend.dto.transaction;

import com.walletiq.backend.enums.TxnType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    BigDecimal amount,

    @NotNull(message = "Transaction type is required")
    TxnType type,

    @NotNull(message = "Date is required")
    LocalDate date,

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    String note, // optional

    // Note: categoryId and paymentModeId are required for EXPENSE, for INCOME these two fields
    // are not required, so we are checking these cases in our service implementation rather
    // using annotation

    String categoryId,

    String paymentModeId

) {
}