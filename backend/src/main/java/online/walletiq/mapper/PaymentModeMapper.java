package online.walletiq.mapper;

import online.walletiq.dto.paymentmode.PaymentModeResponse;
import online.walletiq.entity.PaymentMode;

public final class PaymentModeMapper {

    private PaymentModeMapper() {
    }

    public static PaymentModeResponse toResponse(PaymentMode paymentMode) {
        return new PaymentModeResponse(
            paymentMode.getId().toString(),
            paymentMode.getName(),
            paymentMode.getUser() == null
        );
    }
}
