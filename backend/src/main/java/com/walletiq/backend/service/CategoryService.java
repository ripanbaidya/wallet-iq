package com.walletiq.backend.service;

import com.walletiq.backend.exception.CategoryException;
import com.walletiq.backend.payload.request.CreateCategoryRequest;
import com.walletiq.backend.payload.request.UpdateCategoryRequest;
import com.walletiq.backend.payload.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    /**
     * Returns system default categories + the current user's own categories.
     */
    List<CategoryResponse> getAllCategories();

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
}