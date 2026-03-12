package com.walletiq.controller;

import com.walletiq.dto.paymentmode.CreatePaymentModeRequest;
import com.walletiq.dto.paymentmode.PaymentModeResponse;
import com.walletiq.dto.paymentmode.UpdatePaymentModeRequest;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.PaymentModeService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Payment Modes", description = "APIs for managing transaction payment modes")
public class PaymentModeController {

    private final PaymentModeService paymentModeService;

    @Operation(
        summary = "Get all payment modes",
        description = "Fetches all available payment modes for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment modes retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<PaymentModeResponse>>> getAllPaymentModes() {
        return ResponseUtil.ok(
            "PaymentModes Retrieved Successfully",
            paymentModeService.getAllPaymentModes()
        );
    }

    @Operation(
        summary = "Create a new payment mode",
        description = "Creates a new payment mode such as Cash, UPI, Credit Card, etc."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment mode created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<PaymentModeResponse>> createPaymentMode(
        @Valid @RequestBody CreatePaymentModeRequest request
    ) {
        return ResponseUtil.created(
            "PaymentMode Created Successfully",
            paymentModeService.createPaymentMode(request)
        );
    }

    @Operation(
        summary = "Update an existing payment mode",
        description = "Updates the details of a payment mode using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment mode updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "404", description = "Payment mode not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PaymentModeResponse>> updatePaymentMode(
        @Parameter(description = "Unique identifier of the payment mode", required = true)
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePaymentModeRequest request
    ) {
        return ResponseUtil.ok(
            "PaymentMode Updated Successfully",
            paymentModeService.updatePaymentMode(id, request)
        );
    }

    @Operation(
        summary = "Delete a payment mode",
        description = "Deletes a payment mode using its unique identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Payment mode deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Payment mode not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMode(
        @Parameter(description = "Unique identifier of the payment mode", required = true)
        @PathVariable UUID id
    ) {
        paymentModeService.deletePaymentMode(id);
        return ResponseUtil.noContent();
    }
}