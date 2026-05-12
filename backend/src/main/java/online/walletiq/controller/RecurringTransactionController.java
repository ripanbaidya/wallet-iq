package online.walletiq.controller;

import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.recurringtransaction.ForecastSummaryResponse;
import online.walletiq.dto.recurringtransaction.RecurringTransactionRequest;
import online.walletiq.dto.recurringtransaction.RecurringTransactionResponse;
import online.walletiq.dto.recurringtransaction.UpdateRecurringTransactionRequest;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.service.RecurringTransactionService;
import online.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/recurring")
@RequiredArgsConstructor
@Tag(
    name = "Recurring Transactions",
    description = "APIs for managing recurring income and expense rules"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class RecurringTransactionController {

    private final RecurringTransactionService service;

    @Operation(
        summary = "Create a recurring transaction",
        description = "Creates a recurring transaction rule with a defined schedule and amount."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Recurring transaction created successfully",
            content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload or date range",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> create(
        @Valid @RequestBody RecurringTransactionRequest request
    ) {
        return ResponseUtil.created(
            "Recurring transaction created successfully",
            service.create(request)
        );
    }

    @Operation(
        summary = "Get all recurring transactions",
        description = "Fetches all active recurring transactions for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recurring transactions fetched successfully",
            content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<RecurringTransactionResponse>>> getAll() {
        return ResponseUtil.ok(
            "Recurring transactions fetched successfully",
            service.getAllByUser()
        );
    }

    @Operation(
        summary = "Get recurring transaction by ID",
        description = "Fetches a specific recurring transaction using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recurring transaction fetched successfully",
            content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Recurring transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> getById(

        @Parameter(description = "Unique identifier of the recurring transaction", required = true)
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "Recurring transaction fetched successfully",
            service.getById(id)
        );
    }

    @Operation(
        summary = "Update a recurring transaction",
        description = "Updates an existing recurring transaction. Supports partial updates and validates date range."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recurring transaction updated successfully",
            content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Recurring transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<RecurringTransactionResponse>> update(

        @Parameter(description = "Unique identifier of the recurring transaction", required = true)
        @PathVariable UUID id,

        @Valid @RequestBody UpdateRecurringTransactionRequest request
    ) {
        return ResponseUtil.ok(
            "Recurring transaction updated successfully",
            service.update(id, request)
        );
    }

    @Operation(
        summary = "Deactivate a recurring transaction",
        description = "Soft deletes (deactivates) a recurring transaction so it no longer generates future transactions."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recurring transaction deactivated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Recurring transaction not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deactivate(

        @Parameter(description = "Unique identifier of the recurring transaction", required = true)
        @PathVariable UUID id
    ) {
        service.deactivate(id);
        return ResponseUtil.ok("Recurring transaction deactivated successfully", null);
    }

    @Operation(
        summary = "Get forecast",
        description = "Generates projected income, expenses, and net balance based on active recurring transactions."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Forecast generated successfully",
            content = @Content(schema = @Schema(implementation = ForecastSummaryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid number of days",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/forecast")
    public ResponseEntity<ResponseWrapper<ForecastSummaryResponse>> forecast(

        @Parameter(
            description = "Number of days for forecast (1 to 365)",
            example = "30"
        )
        @RequestParam(defaultValue = "30")
        @Min(1) @Max(365) int days
    ) {
        return ResponseUtil.ok(
            "Forecast generated successfully",
            service.forecast(days)
        );
    }
}