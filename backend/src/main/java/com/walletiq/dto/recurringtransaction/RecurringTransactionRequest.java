package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.RecurringFrequency;
import com.walletiq.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request payload to create a recurring transaction")
public record RecurringTransactionRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(
        description = "Title of the recurring transaction",
        example = "Wifi Bill"
    )
    String title,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits and 2 decimal places")
    @Schema(
        description = "Transaction amount",
        example = "500.00"
    )
    BigDecimal amount,

    @NotNull(message = "Transaction type is required")
    @Schema(
        description = "Type of transaction",
        allowableValues = {"INCOME", "EXPENSE"},
        example = "EXPENSE"
    )
    TxnType type,

    @NotNull(message = "Recurring frequency is required")
    @Schema(
        description = "Frequency of the recurring transaction",
        allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"},
        example = "MONTHLY"
    )
    RecurringFrequency frequency,

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @Schema(
        description = "Date when the recurring transaction starts",
        example = "2026-04-01"
    )
    LocalDate startDate,

    @Future(message = "End date must be in the future")
    @Schema(
        description = "Optional end date of the recurring transaction. Must be after startDate",
        example = "2027-04-01"
    )
    LocalDate endDate,

    @Size(max = 255, message = "Note must not exceed 255 characters")
    @Schema(
        description = "Optional note for the transaction",
        example = "Monthly wifi bill"
    )
    String note,

    @NotNull(message = "Category is required")
    @Schema(description = "Category ID associated with the transaction")
    UUID categoryId,

    @NotNull(message = "Payment mode is required")
    @Schema(description = "Payment mode ID used for the transaction")
    UUID paymentModeId

) {
}