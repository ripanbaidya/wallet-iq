package online.walletiq.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Schema(description = "Response payload representing a monthly budget for a specific category")
public record BudgetResponse(

    @Schema(
        description = "Unique identifier of the budget record",
        example = "1f7d9e72-3e7d-4a23-bc21-9c7e8c9a1a10"
    )
    UUID id,

    @Schema(
        description = "Display name of the category associated with this budget",
        example = "Food"
    )
    String categoryName,

    @Schema(
        description = "Unique identifier of the category",
        example = "2f5e9a51-5b2b-4e30-9c1f-8c2c5f6a2d45"
    )
    UUID categoryId,

    @Schema(
        description = "Budget month in format yyyy-MM",
        example = "2026-04",
        type = "string"
    )
    YearMonth month,

    @Schema(
        description = "Maximum spending limit allowed for this category during the specified month",
        example = "5000.00"
    )
    BigDecimal limitAmount,

    @Schema(
        description = "Percentage threshold that triggers a budget alert when spending approaches the limit",
        example = "80",
        minimum = "1",
        maximum = "100"
    )
    int alertThreshold

) {
}