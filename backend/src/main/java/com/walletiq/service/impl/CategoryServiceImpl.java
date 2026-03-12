package com.walletiq.service.impl;

import com.walletiq.entity.Category;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.CategoryException;
import com.walletiq.mapper.CategoryMapper;
import com.walletiq.dto.categories.CreateCategoryRequest;
import com.walletiq.dto.categories.UpdateCategoryRequest;
import com.walletiq.dto.categories.CategoryResponse;
import com.walletiq.repository.CategoryRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.CategoryService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // Get all system defaults + current user's categories

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllVisibleToUser(currentUser()).stream()
            .map(CategoryMapper::mapToCategoryResponse)
            .toList();
    }

    // Create new category for current user

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        User currentUser = currentUser();

        if (categoryRepository.existsByNameIgnoreCaseAndUser(request.name(), currentUser)) {
            throw new CategoryException(ErrorCode.CATEGORY_ALREADY_EXISTS,
                "A category named '%s' already exists".formatted(request.name())
            );
        }

        // Create and save category
        Category category = new Category();
        category.setName(request.name().trim());
        category.setUser(currentUser);

        return CategoryMapper.mapToCategoryResponse(categoryRepository.save(category));
    }

    // Update category name for current user

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        User currentUser = currentUser();

        Category category = findCategoryByIdAndUser(id, currentUser);

        String newName = request.name().trim();

        // Skip duplicate check if name hasn't changed (case-insensitive)
        boolean nameChanged = !category.getName().equalsIgnoreCase(newName);
        if (nameChanged && categoryRepository.existsByNameIgnoreCaseAndUser(newName, currentUser)) {
            throw new CategoryException(
                ErrorCode.CATEGORY_ALREADY_EXISTS,
                "A category named '%s' already exists".formatted(newName)
            );
        }

        category.setName(newName);
        return CategoryMapper.mapToCategoryResponse(categoryRepository.save(category));
    }

    // Delete category for current user

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        User currentUser = currentUser();

        Category category = findCategoryByIdAndUser(id, currentUser);

        categoryRepository.delete(category);
    }

    // Private helper methods

    // return proxy object
    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    // Find category belong to specific user
    private Category findCategoryByIdAndUser(UUID id, User user) {
        // findByIdAndUser ensures the user can only update their OWN categories
        // System defaults (user IS NULL) will never match here — intentional
        return categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new CategoryException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found or you do not have permission to delete it"
            ));
    }
}