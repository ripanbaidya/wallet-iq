package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.RecurringFrequency;
import com.walletiq.enums.TxnType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RecurringTransactionResponse(

    UUID id,
    String title,
    BigDecimal amount,
    TxnType type,
    RecurringFrequency frequency,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate nextExecutionDate,
    boolean isActive,
    String note,
    String categoryName,
    String paymentModeName,
    String createdAt

) {
}