package com.walletiq.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public final class PageableValidator {

    private PageableValidator() {
    }

    private static final Set<String> TRANSACTION_SORTABLE_FIELDS = Set.of(
        "date", "amount", "type", "createdAt");

    public static Pageable validateTransactionPageable(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
            .filter(order -> TRANSACTION_SORTABLE_FIELDS.contains(order.getProperty()))
            .toList();

        if (safeOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "date")
            );
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by(safeOrders)
        );
    }
}