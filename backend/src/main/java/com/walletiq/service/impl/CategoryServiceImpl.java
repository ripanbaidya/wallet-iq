package com.walletiq.service.impl;

import com.walletiq.constant.CacheNames;
import com.walletiq.dto.categories.CategoryResponse;
import com.walletiq.dto.categories.CreateCategoryRequest;
import com.walletiq.dto.categories.UpdateCategoryRequest;
import com.walletiq.entity.Category;
import com.walletiq.entity.User;
import com.walletiq.enums.CategoryType;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.CategoryException;
import com.walletiq.mapper.CategoryMapper;
import com.walletiq.repository.CategoryRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.CategoryService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link CategoryService}.
 * <p>Handles category management for the current user, including creation,
 * update, deletion, and retrieval of both user-defined and system default
 * categories.
 * <p>Also manages cache invalidation for category-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CATEGORIES, keyGenerator = "userKeyGenerator")
    public List<CategoryResponse> getAll(CategoryType categoryType) {
        User user = currentUser();

        return categoryRepository.findAllVisibleToUser(user, categoryType)
            .stream()
            .map(CategoryMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true)
    public CategoryResponse create(CreateCategoryRequest request) {
        User user = currentUser();

        String name = request.name().trim();
        CategoryType categoryType = request.categoryType();

        if (categoryRepository.existsByNameIgnoreCaseAndUserAndCategoryType(name, user, categoryType)) {
            throw new CategoryException(ErrorCode.CATEGORY_ALREADY_EXISTS,
                "A category named '%s' already exists".formatted(name)
            );
        }

        Category category = new Category();
        category.setName(name);
        category.setCategoryType(request.categoryType());
        category.setUser(user);

        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true)
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        User user = currentUser();
        Category category = findCategoryByIdAndUser(id, user);

        String newName = request.name().trim();
        CategoryType newType = request.categoryType() != null ? request.categoryType()
            : category.getCategoryType();

        log.debug("Updating category {} for user {} with name '{}' and type '{}'",
            id, user.getId(), newName, newType);

        boolean nameChanged = !category.getName().equalsIgnoreCase(newName);
        boolean typeChanged = newType != category.getCategoryType();

        // Check duplicates only when name or type changes
        if ((nameChanged || typeChanged)
            && categoryRepository.existsByNameIgnoreCaseAndUserAndCategoryType(newName, user, newType)) {

            throw new CategoryException(ErrorCode.CATEGORY_ALREADY_EXISTS,
                "A category named '%s' already exists".formatted(newName)
            );
        }

        // Apply updates
        category.setName(newName);
        category.setCategoryType(newType);

        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true)
    public void delete(UUID id) {
        User currentUser = currentUser();

        Category category = findCategoryByIdAndUser(id, currentUser);

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    /**
     * Find category belong to specific user
     */
    private Category findCategoryByIdAndUser(UUID id, User user) {
        // findByIdAndUser ensures the user can only update their OWN categories
        // System defaults (user IS NULL) will never match here — intentional
        return categoryRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found or you do not have permission to delete it"
            ));
    }
}