package com.walletiq.mapper;

import com.walletiq.dto.transaction.TransactionResponse;
import com.walletiq.entity.Category;
import com.walletiq.entity.PaymentMode;
import com.walletiq.entity.Transaction;

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
