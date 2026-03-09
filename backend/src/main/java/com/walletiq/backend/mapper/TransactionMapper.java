package com.walletiq.backend.mapper;

import com.walletiq.backend.dto.transaction.TransactionResponse;
import com.walletiq.backend.entity.Category;
import com.walletiq.backend.entity.PaymentMode;
import com.walletiq.backend.entity.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(Transaction t) {
        Category category = t.getCategory();
        PaymentMode paymentMode = t.getPaymentMode();

        return new TransactionResponse(
            t.getId().toString(),
            t.getAmount(),
            t.getType(),
            t.getDate(),
            t.getNote(),

            category != null ? category.getId().toString() : null,
            category != null ? category.getName() : null,

            paymentMode != null ? paymentMode.getId().toString() : null,
            paymentMode != null ? paymentMode.getName() : null,

            t.getEmbeddingId()
        );
    }
}
