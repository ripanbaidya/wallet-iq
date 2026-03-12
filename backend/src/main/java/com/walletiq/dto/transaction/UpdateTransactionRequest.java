package com.walletiq.dto.transaction;

import com.walletiq.enums.TxnType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    BigDecimal amount,

    TxnType type,

    LocalDate date,

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    String note, // optional

    String categoryId,

    String paymentModeId

) {
}