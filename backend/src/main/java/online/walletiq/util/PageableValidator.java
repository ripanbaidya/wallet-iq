package online.walletiq.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

/**
 * Utility class for validating and sanitizing {@link Pageable} parameters
 * before they are passed to repository queries.
 * Prevents arbitrary field injection in sort parameters by whitelisting
 * only known, safe field names per entity type.
 */
public final class PageableValidator {

    /**
     * Allowed sort fields for transaction queries.
     */
    private static final Set<String> TRANSACTION_SORTABLE_FIELDS = Set.of(
        "date", "amount", "type", "createdAt"
    );

    private PageableValidator() {
    }

    /**
     * Validates the sort parameters of the given {@link Pageable} against the
     * allowed transaction fields.
     * If the pageable is unsorted, it is returned as-is, If all sort fields are
     * invalid, falls back to {@code date DESC}.
     * Otherwise, only the valid sort orders are retained.
     *
     * @param pageable the incoming pageable from the controller
     * @return a sanitized {@link Pageable} safe to pass to the repository
     */
    public static Pageable validateTransactionPageable(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
            .filter(order -> TRANSACTION_SORTABLE_FIELDS.contains(order.getProperty()))
            .toList();

        if (safeOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "date"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by(safeOrders));
    }
}