package com.walletiq.mapper;

import com.walletiq.dto.paymentmode.PaymentModeResponse;
import com.walletiq.entity.PaymentMode;

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
