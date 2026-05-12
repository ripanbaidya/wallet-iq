package online.walletiq.dto.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.domain.Page;

/**
 * Record representing pagination metadata for paginated responses.
 * Maps directly to Spring Data's Page interface.
 *
 * @param number        Current page number (0-indexed)
 * @param size          Number of items per page
 * @param totalElements Total number of items across all pages
 * @param totalPages    Total number of pages
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"number", "size", "totalElements", "totalPages"})
public record PageInfo(
    int number,
    int size,
    long totalElements,
    int totalPages
) {

    public static PageInfo from(Page<?> page) {
        return new PageInfo(page.getNumber(), page.getSize(), page.getTotalElements(),
            page.getTotalPages());
    }
}