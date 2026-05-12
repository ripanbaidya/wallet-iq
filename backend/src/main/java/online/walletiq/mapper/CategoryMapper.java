package online.walletiq.mapper;

import online.walletiq.entity.Category;
import online.walletiq.dto.categories.CategoryResponse;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId().toString(),
            category.getName(),
            category.getCategoryType(),
            category.getUser() == null
        );
    }
}
