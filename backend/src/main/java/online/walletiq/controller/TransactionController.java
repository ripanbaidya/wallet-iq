package online.walletiq.controller;

import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.dto.transaction.CreateTransactionRequest;
import online.walletiq.dto.transaction.TransactionFilterRequest;
import online.walletiq.dto.transaction.TransactionResponse;
import online.walletiq.dto.transaction.UpdateTransactionRequest;
import online.walletiq.enums.TxnType;
import online.walletiq.service.TransactionService;
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
@Tag(
    name = "Transactions",
    description = "APIs for managing financial transactions"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Admin access required",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
})
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
        summary = "Get all transactions",
        description = "Fetches transactions with optional filters such as type, category, and date range. Supports pagination and sorting."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Transactions fetched successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid filter parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllTransactions(
        @Parameter(description = "Transaction type filter (INCOME or EXPENSE)")
        @RequestParam(required = false) TxnType type,

        @Parameter(description = "Category ID to filter transactions")
        @RequestParam(required = false) String categoryId,

        @Parameter(description = "Start date (inclusive) in ISO format yyyy-MM-dd")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateFrom,

        @Parameter(description = "End date (inclusive) in ISO format yyyy-MM-dd")
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
            new TransactionFilterRequest(
                type,
                categoryId != null ? UUID.fromString(categoryId) : null,
                dateFrom,
                dateTo
            );

        Page<TransactionResponse> page =
            transactionService.getAllTransactions(filter, pageable);

        return ResponseUtil.paginated("Transactions fetched successfully", page);
    }


    @Operation(
        summary = "Get transaction by ID",
        description = "Fetches a specific transaction using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Transaction fetched successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
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
        description = "Creates a new transaction (INCOME or EXPENSE). Validates category and payment mode."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Transaction created successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
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
        description = "Updates an existing transaction. Supports partial updates and validates ownership."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Transaction updated successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
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
        @ApiResponse(
            responseCode = "204",
            description = "Transaction deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
        @Parameter(description = "Unique identifier of the transaction", required = true)
        @PathVariable UUID id
    ) {
        transactionService.deleteTransaction(id);
        return ResponseUtil.noContent();
    }
}