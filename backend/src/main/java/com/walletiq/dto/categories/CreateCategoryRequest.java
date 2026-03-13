package com.walletiq.dto.categories;

import com.walletiq.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

    @Schema(
        description = "Name of the category",
        example = "Food",
        minLength = 1,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    String name,

    @Schema(
        description = "Type of category",
        example = "EXPENSE",
        allowableValues = {"INCOME", "EXPENSE"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Category type must not be null")
    CategoryType categoryType

) {
}