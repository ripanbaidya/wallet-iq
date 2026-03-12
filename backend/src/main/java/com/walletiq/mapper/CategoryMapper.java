package com.walletiq.mapper;

import com.walletiq.entity.Category;
import com.walletiq.dto.categories.CategoryResponse;

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
