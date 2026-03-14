package com.walletiq.controller;

import com.walletiq.dto.budget.BudgetRequest;
import com.walletiq.dto.budget.BudgetResponse;
import com.walletiq.dto.budget.BudgetStatusResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.BudgetService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(
        summary = "Create a new budget",
        description = "Creates a budget for a specific category and month."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<BudgetResponse>> create(
        @Valid
        @RequestBody
        @Parameter(description = "Budget creation request payload")
        BudgetRequest request) {

        return ResponseUtil.ok("Budget created.", budgetService.create(request));
    }


    @Operation(
        summary = "Get budgets by month",
        description = "Fetch all budgets for the authenticated user for a given month"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budgets fetched successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid month format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BudgetResponse>>> getByMonth(

        @Parameter(
            description = "Month for which budgets should be retrieved (format: YYYY-MM)",
            example = "2026-03"
        )
        @RequestParam YearMonth month
    ) {

        return ResponseUtil.ok("Budgets fetched", budgetService.getByMonth(month));
    }


    @Operation(
        summary = "Get budget status",
        description = "Returns current status of a budget including spent amount and remaining amount"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget status fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<ResponseWrapper<BudgetStatusResponse>> getStatus(

        @Parameter(
            description = "Budget ID",
            example = "c3f6f4a2-3e1a-4c89-bb72-123456789abc"
        )
        @PathVariable UUID id) {

        return ResponseUtil.ok("Budget status fetched", budgetService.getStatus(id));
    }
}