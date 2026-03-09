package com.walletiq.backend.dto.paymentmode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePaymentModeRequest(

    @NotBlank(message = "Payment mode name must not be blank")
    @Size(min = 1, max = 100, message = "Payment mode name must be between 1 and 100 characters")
    String name

) {
}