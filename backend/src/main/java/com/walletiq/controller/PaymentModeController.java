package com.walletiq.controller;

import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.paymentmode.CreatePaymentModeRequest;
import com.walletiq.dto.paymentmode.PaymentModeResponse;
import com.walletiq.dto.paymentmode.UpdatePaymentModeRequest;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.PaymentModeService;
import com.walletiq.util.ResponseUtil;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment-modes")
@RequiredArgsConstructor
@Tag(
    name = "Payment Modes",
    description = "APIs for managing transaction payment modes"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class PaymentModeController {

    private final PaymentModeService paymentModeService;

    @Operation(
        summary = "Get all payment modes",
        description = "Fetches all available payment modes for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Payment modes fetched successfully",
            content = @Content(schema = @Schema(implementation = PaymentModeResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<PaymentModeResponse>>> getAllPaymentModes() {
        return ResponseUtil.ok(
            "Payment modes fetched successfully",
            paymentModeService.getAll()
        );
    }

    @Operation(
        summary = "Create a new payment mode",
        description = "Creates a new payment mode such as Cash, UPI, Credit Card, etc."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Payment mode created successfully",
            content = @Content(schema = @Schema(implementation = PaymentModeResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<PaymentModeResponse>> createPaymentMode(
        @Valid @RequestBody CreatePaymentModeRequest request
    ) {
        return ResponseUtil.created(
            "Payment mode created successfully",
            paymentModeService.create(request)
        );
    }

    @Operation(
        summary = "Update a payment mode",
        description = "Updates an existing payment mode using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Payment mode updated successfully",
            content = @Content(schema = @Schema(implementation = PaymentModeResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment mode not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PaymentModeResponse>> updatePaymentMode(
        @Parameter(description = "Unique identifier of the payment mode", required = true)
        @PathVariable UUID id,

        @Valid @RequestBody UpdatePaymentModeRequest request
    ) {
        return ResponseUtil.ok(
            "Payment mode updated successfully",
            paymentModeService.update(id, request)
        );
    }

    @Operation(
        summary = "Delete a payment mode",
        description = "Deletes a payment mode using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Payment mode deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment mode not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMode(
        @Parameter(description = "Unique identifier of the payment mode", required = true)
        @PathVariable UUID id
    ) {
        paymentModeService.delete(id);
        return ResponseUtil.noContent();
    }
}