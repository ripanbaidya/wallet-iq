package com.walletiq.backend.mapper;

import com.walletiq.backend.dto.paymentmode.PaymentModeResponse;
import com.walletiq.backend.entity.PaymentMode;

public final class PaymentModeMapper {

    private PaymentModeMapper() {
    }

    public static PaymentModeResponse toResponse(PaymentMode paymentMode) {
        return new PaymentModeResponse(
            paymentMode.getId().toString(),
            paymentMode.getName(),
            paymentMode.getUser() == null       // null user = system default
        );
    }
}
