package com.walletiq.dto.paymentmode;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment mode returned by the API")
public record PaymentModeResponse(

    @Schema(
        description = "Unique identifier of the payment mode",
        example = "d3f5c6a1-9f7b-4b3a-8b1d-2a4c6e7f8a90"
    )
    String id,

    @Schema(
        description = "Name of the payment mode",
        example = "UPI"
    )
    String name,

    @Schema(
        description = "Indicates whether this payment mode is a system default",
        example = "true"
    )
    boolean isDefault

) {
}