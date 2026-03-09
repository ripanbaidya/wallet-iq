package com.walletiq.backend.dto.categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    String name

) {
}