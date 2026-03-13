package com.walletiq.dto.recurringtransaction;

import com.walletiq.enums.TxnType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ForecastEntryResponse(

    LocalDate projectedDate,
    String title,
    BigDecimal amount,
    TxnType type,
    String categoryName

) {
}