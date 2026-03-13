package com.walletiq.dto.recurringtransaction;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ForecastSummaryResponse(

    int forecastDays,
    BigDecimal projectedIncome,
    BigDecimal projectedExpense,
    BigDecimal projectedNetBalance,
    List<ForecastEntryResponse> entries

) {
}