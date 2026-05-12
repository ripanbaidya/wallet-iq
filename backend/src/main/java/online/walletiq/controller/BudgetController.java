package online.walletiq.controller;

import online.walletiq.dto.budget.BudgetRequest;
import online.walletiq.dto.budget.BudgetResponse;
import online.walletiq.dto.budget.BudgetStatusResponse;
import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.service.BudgetService;
import online.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Tag(
    name = "Budgets",
    description = "APIs for managing monthly budgets and tracking spending status"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(
        summary = "Create a new budget",
        description = "Creates a budget for a specific category and month. Ensures uniqueness per category per month."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Budget created successfully",
            content = @Content(schema = @Schema(implementation = BudgetResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or duplicate budget",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<BudgetResponse>> create(

        @Valid @RequestBody BudgetRequest request
    ) {
        return ResponseUtil.created(
            "Budget created successfully",
            budgetService.create(request)
        );
    }

    @Operation(
        summary = "Get budgets by month",
        description = "Fetches all budgets for the authenticated user for a given month."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Budgets fetched successfully",
            content = @Content(schema = @Schema(implementation = BudgetResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid month format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BudgetResponse>>> getByMonth(

        @Parameter(
            description = "Month in format YYYY-MM",
            example = "2026-03",
            required = true
        )
        @RequestParam YearMonth month
    ) {
        return ResponseUtil.ok(
            "Budgets fetched successfully",
            budgetService.getByMonth(month)
        );
    }

    @Operation(
        summary = "Get budget status",
        description = "Returns current budget status including spent amount, remaining amount, and usage percentage."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Budget status fetched successfully",
            content = @Content(schema = @Schema(implementation = BudgetStatusResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Budget not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<ResponseWrapper<BudgetStatusResponse>> getStatus(

        @Parameter(
            description = "Unique identifier of the budget",
            example = "c3f6f4a2-3e1a-4c89-bb72-123456789abc",
            required = true
        )
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "Budget status fetched successfully",
            budgetService.getStatus(id)
        );
    }
}