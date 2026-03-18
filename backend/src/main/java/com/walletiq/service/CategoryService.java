package com.walletiq.service;

import com.walletiq.dto.categories.CategoryResponse;
import com.walletiq.dto.categories.CreateCategoryRequest;
import com.walletiq.dto.categories.UpdateCategoryRequest;
import com.walletiq.entity.Category;
import com.walletiq.enums.CategoryType;
import com.walletiq.exception.CategoryException;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    /**
     * Retrieves all categories visible to the current user.
     * <p>Includes both system default categories and user-defined categories,
     * filtered by category type.
     *
     * @param type type of category (INCOME / EXPENSE)
     * @return list of category responses
     */
    List<CategoryResponse> getAll(CategoryType type);

    /**
     * Creates a new category for the current user.
     * <p>Ensures that no duplicate category exists with the same name
     * (case-insensitive) and type for the user.
     *
     * @param request category creation request
     * @return created category response
     * @throws CategoryException if a category with the same name already exists
     */
    CategoryResponse create(CreateCategoryRequest request);

    /**
     * Updates an existing category owned by the current user.
     * <p>Supports updating name and type. Duplicate validation is applied
     * only when name or type changes.
     *
     * @param id      category identifier
     * @param request update request
     * @return updated category response
     * @throws CategoryException if category is not found, not owned by user,
     *                           or a duplicate category exists
     */
    CategoryResponse update(UUID id, UpdateCategoryRequest request);

    /**
     * Deletes a category owned by the current user.
     *
     * @param id category identifier
     * @throws CategoryException if category is not found or not owned by user
     */
    void delete(UUID id);

    /**
     * Retrieves a category by its ID.
     *
     * @param id category identifier
     * @return category entity
     * @throws CategoryException if category is not found
     */
    Category findById(UUID id);

}