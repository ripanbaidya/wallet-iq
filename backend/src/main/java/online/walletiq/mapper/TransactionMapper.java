package online.walletiq.mapper;

import online.walletiq.dto.dashboard.CategoryBreakdownItem;
import online.walletiq.dto.dashboard.CategoryBreakdownResponse;
import online.walletiq.dto.transaction.TransactionResponse;
import online.walletiq.entity.Category;
import online.walletiq.entity.PaymentMode;
import online.walletiq.entity.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

    public static List<CategoryBreakdownResponse> toBreakdownResponse(
        List<CategoryBreakdownItem> items,
        BigDecimal total
    ) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return items.stream()
                .map(i -> new CategoryBreakdownResponse(i.categoryName(), i.amount(), 0.0))
                .toList();
        }

        return items.stream()
            .map(i -> {
                double pct = i.amount()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
                return new CategoryBreakdownResponse(i.categoryName(), i.amount(), pct);
            })
            .toList();
    }
}
