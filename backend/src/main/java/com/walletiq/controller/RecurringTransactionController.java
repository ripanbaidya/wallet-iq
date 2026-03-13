package com.walletiq.controller;

import com.walletiq.dto.recurringtransaction.ForecastSummaryResponse;
import com.walletiq.dto.recurringtransaction.RecurringTransactionRequest;
import com.walletiq.dto.recurringtransaction.RecurringTransactionResponse;
import com.walletiq.dto.recurringtransaction.UpdateRecurringTransactionRequest;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.RecurringTransactionService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recurring")
@RequiredArgsConstructor
@Tag(name = "Recurring Transactions", description = "Manage recurring income and expense rules")
public class RecurringTransactionController {

    private final RecurringTransactionService service;

    @PostMapping
    @Operation(summary = "Create a recurring transaction")
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> create(
        @Valid @RequestBody RecurringTransactionRequest request
    ) {

        var response = service.create(request);

        return ResponseUtil.created("Recurring transaction created",
            response);
    }

    @GetMapping
    @Operation(summary = "Get all active recurring transactions for current user")
    public ResponseEntity<ResponseWrapper<List<RecurringTransactionResponse>>> getAll() {

        var responses = service.getAllByUser();

        return ResponseUtil.ok("Recurring transactions fetched", responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recurring transaction by ID")
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> getById(
        @PathVariable UUID id
    ) {

        var response = service.getById(id);

        return ResponseUtil.ok("Recurring transaction fetched", response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a recurring transaction")
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRecurringTransactionRequest request
    ) {

        var response = service.update(id, request);

        return ResponseUtil.ok("Recurring transaction updated", response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a recurring transaction (soft delete)")
    public ResponseEntity<ResponseWrapper<Void>> deactivate(@PathVariable UUID id) {

        service.deactivate(id);
        return ResponseUtil.ok("Recurring transaction deactivated", null);
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get projected cash flow for next N days")
    public ResponseEntity<ResponseWrapper<ForecastSummaryResponse>> forecast(
        @RequestParam(defaultValue = "30")
        @Min(value = 1, message = "Forecast days must be at least 1")
        @Max(value = 365, message = "Forecast days cannot exceed 365")
        int days
    ) {

        var forecast = service.forecast(days);
        return ResponseUtil.ok("Forecast generated", forecast);
    }
}