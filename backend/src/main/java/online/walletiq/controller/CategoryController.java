package online.walletiq.controller;

import online.walletiq.dto.categories.CreateCategoryRequest;
import online.walletiq.dto.categories.UpdateCategoryRequest;
import online.walletiq.dto.categories.CategoryResponse;
import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.enums.CategoryType;
import online.walletiq.service.CategoryService;
import online.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(
    name = "Categories",
    description = "APIs for managing transaction categories"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Get all categories",
        description = "Fetches all categories for the authenticated user filtered by category type (INCOME or EXPENSE)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Categories fetched successfully",
            content = @Content(schema = @Schema(implementation = ResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid category type",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CategoryResponse>>> getAllCategories(
        @RequestParam("type") CategoryType type
    ) {
        return ResponseUtil.ok(
            "Categories fetched successfully",
            categoryService.getAll(type)
        );
    }

    @Operation(
        summary = "Create a new category",
        description = "Creates a new category for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<CategoryResponse>> createCategory(
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseUtil.created(
            "Category created successfully",
            categoryService.create(request)
        );
    }

    @Operation(
        summary = "Update a category",
        description = "Updates an existing category using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CategoryResponse>> updateCategory(
        @PathVariable UUID id,

        @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseUtil.ok(
            "Category updated successfully",
            categoryService.update(id, request)
        );
    }

    @Operation(
        summary = "Delete a category",
        description = "Deletes a category using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Category deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
        @PathVariable UUID id
    ) {
        categoryService.delete(id);
        return ResponseUtil.noContent();
    }
}