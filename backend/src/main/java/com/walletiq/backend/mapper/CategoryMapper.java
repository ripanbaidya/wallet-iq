package com.walletiq.backend.mapper;

import com.walletiq.backend.entity.Category;
import com.walletiq.backend.dto.categories.CategoryResponse;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
            category.getId().toString(),
            category.getName(),
            category.getUser() == null     // null user = system default
        );
    }
}
