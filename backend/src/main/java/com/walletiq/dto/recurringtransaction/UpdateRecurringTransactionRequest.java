package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.RecurringFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request payload to update an existing recurring transaction.")
public record UpdateRecurringTransactionRequest(

    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(
        description = "Updated title of the recurring transaction",
        example = "Updated Wifi Bill"
    )
    String title,

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits and 2 decimal places")
    @Schema(
        description = "Updated transaction amount",
        example = "650.00"
    )
    BigDecimal amount,

    @Schema(
        description = "Updated recurring frequency",
        allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"},
        example = "MONTHLY"
    )
    RecurringFrequency frequency,

    @Schema(
        description = "Updated end date for the recurring transaction",
        example = "2027-04-01"
    )
    LocalDate endDate,

    @Size(max = 255, message = "Note must not exceed 255 characters")
    @Schema(
        description = "Updated note for the transaction",
        example = "Updated monthly wifi bill"
    )
    String note,

    @Schema(description = "Updated category ID")
    UUID categoryId,

    @Schema(description = "Updated payment mode ID")
    UUID paymentModeId

) {
}