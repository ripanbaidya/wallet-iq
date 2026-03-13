package com.walletiq.service;

import com.walletiq.entity.Category;
import com.walletiq.enums.CategoryType;
import com.walletiq.exception.CategoryException;
import com.walletiq.dto.categories.CreateCategoryRequest;
import com.walletiq.dto.categories.UpdateCategoryRequest;
import com.walletiq.dto.categories.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    /**
     * Returns system default categories + the current user's own categories based
     * on the category types
     */
    List<CategoryResponse> getAllCategories(CategoryType type);

    /**
     * Creates a new category owned by the current user.
     *
     * @throws CategoryException if a category with the same name already exists for this user.
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Updates a category owned by the current user.
     *
     * @throws CategoryException if not found, not owned by user, or name already taken.
     */
    CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);

    /**
     * Deletes a category owned by the current user.
     *
     * @throws CategoryException if not found or not owned by user.
     */
    void deleteCategory(UUID id);

    /**
     * find category by id
     *
     * @throws CategoryException if not found
     */
    Category findById(UUID id);

}