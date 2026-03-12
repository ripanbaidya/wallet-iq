package com.walletiq.controller;

import com.walletiq.dto.categories.CreateCategoryRequest;
import com.walletiq.dto.categories.UpdateCategoryRequest;
import com.walletiq.dto.categories.CategoryResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.CategoryService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "APIs for managing transaction categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Get all categories",
        description = "Fetches all categories available for the authenticated user."
    )
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CategoryResponse>>> getAllCategories() {
        return ResponseUtil.ok("Categories fetched successfully",
            categoryService.getAllCategories());
    }

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<ResponseWrapper<CategoryResponse>> createCategory(
        @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseUtil.created("Category created successfully",
            categoryService.createCategory(request));
    }

    @Operation(summary = "Update an existing category")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CategoryResponse>> updateCategory(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseUtil.ok("Category updated successfully",
            categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseUtil.noContent();
    }
}