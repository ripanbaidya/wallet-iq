package online.walletiq.controller;

import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.subscription.CreateOrderResponse;
import online.walletiq.dto.subscription.SubscriptionStatusResponse;
import online.walletiq.dto.subscription.VerifyPaymentRequest;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.service.SubscriptionService;
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

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(
    name = "Subscriptions",
    description = "APIs for managing subscription lifecycle, payments, and status"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
})
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
        summary = "Create Razorpay order",
        description = "Creates a new Razorpay order for subscription purchase. " +
            "Fails if user already has an active subscription."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = CreateOrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Subscription already active or invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Failed to create Razorpay order",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/order")
    public ResponseEntity<ResponseWrapper<CreateOrderResponse>> createOrder() {
        return ResponseUtil.created(
            "Order created successfully",
            subscriptionService.createOrder()
        );
    }

    @Operation(
        summary = "Verify Razorpay payment",
        description = "Verifies Razorpay payment signature and activates the subscription. " +
            "This should be called after successful payment on frontend."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Payment verified and subscription activated",
            content = @Content(schema = @Schema(implementation = ResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload or subscription not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid Razorpay signature",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/verify")
    public ResponseEntity<ResponseWrapper<Void>> verifyPayment(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Razorpay payment verification payload",
            required = true,
            content = @Content(
                schema = @Schema(implementation = VerifyPaymentRequest.class)
            )
        )
        @RequestBody VerifyPaymentRequest request
    ) {
        subscriptionService.verifyPayment(request);
        return ResponseUtil.ok("Subscription activated successfully");
    }

    @Operation(
        summary = "Get subscription status",
        description = "Returns the current user's subscription status including expiry details"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Subscription status fetched successfully",
            content = @Content(
                schema = @Schema(implementation = SubscriptionStatusResponse.class)
            )
        )
    })
    @GetMapping("/status")
    public ResponseEntity<ResponseWrapper<SubscriptionStatusResponse>> getStatus() {
        return ResponseUtil.ok(
            "Subscription status fetched",
            subscriptionService.getStatus()
        );
    }
}