package com.walletiq.controller;

import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.dto.transaction.CreateTransactionRequest;
import com.walletiq.dto.transaction.TransactionFilterRequest;
import com.walletiq.dto.transaction.TransactionResponse;
import com.walletiq.dto.transaction.UpdateTransactionRequest;
import com.walletiq.enums.TxnType;
import com.walletiq.service.TransactionService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "APIs for managing financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
        summary = "Get all transactions",
        description = "Fetch transactions with optional filters such as transaction type, category, and date range."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transactions fetched successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllTransactions(
        @RequestParam(required = false) TxnType type,
        @RequestParam(required = false) String categoryId,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateFrom,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateTo,

        @Parameter(hidden = true)
        @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("dateFrom must not be after dateTo");
        }

        TransactionFilterRequest filter =
            new TransactionFilterRequest(type, categoryId, dateFrom, dateTo);

        Page<TransactionResponse> page =
            transactionService.getAllTransactions(filter, pageable);

        return ResponseUtil.paginated("Transactions fetched successfully", page);
    }

    @Operation(
        summary = "Get transaction by ID",
        description = "Fetch a specific transaction using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TransactionResponse>> getTransactionById(
        @Parameter(description = "Unique identifier of the transaction", required = true)
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "Transaction fetched successfully",
            transactionService.getTransactionById(id)
        );
    }

    @Operation(
        summary = "Create a transaction",
        description = "Creates a new transaction such as income or expense."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<TransactionResponse>> createTransaction(
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        return ResponseUtil.created(
            "Transaction Created Successfully",
            transactionService.createTransaction(request)
        );
    }

    @Operation(
        summary = "Update a transaction",
        description = "Updates an existing transaction using its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TransactionResponse>> updateTransaction(
        @Parameter(description = "Unique identifier of the transaction", required = true)
        @PathVariable UUID id,
        @Valid @RequestBody UpdateTransactionRequest request
    ) {
        return ResponseUtil.ok(
            "Transaction Updated Successfully",
            transactionService.updateTransaction(id, request)
        );
    }

    @Operation(
        summary = "Delete a transaction",
        description = "Deletes a transaction using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteTransaction(
        @Parameter(description = "Unique identifier of the transaction", required = true)
        @PathVariable UUID id
    ) {
        transactionService.deleteTransaction(id);
        return ResponseUtil.ok("Transaction deleted successfully");
    }
}