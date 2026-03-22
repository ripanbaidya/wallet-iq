package com.walletiq.util;

import com.walletiq.dto.success.PageInfo;
import com.walletiq.dto.success.ResponseWrapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Factory utility for building standard {@link ResponseEntity} responses
 * wrapped in a {@link ResponseWrapper}.
 * Centralizes response construction so controllers stay free of boilerplate
 * HTTP status and body assembly code.
 */
public final class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * Returns a {@code 200 OK} response with no body data.
     *
     * @param message human-readable success message
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message) {
        return ResponseEntity.ok(ResponseWrapper.ok(message));
    }

    /**
     * Returns a {@code 200 OK} response with a data payload.
     *
     * @param message human-readable success message
     * @param data    response payload
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message, T data) {
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Returns a {@code 201 Created} response with the newly created resource.
     *
     * @param message human-readable success message
     * @param data    the created resource
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(message, data));
    }

    /**
     * Returns a {@code 202 Accepted} response with a data payload.
     *
     * @param message human-readable success message
     * @param data    response payload
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> accepted(String message, T data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseWrapper.accepted(message, data));
    }

    /**
     * Returns a {@code 204 No Content} response with an empty body.
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns a {@code 200 OK} response containing paginated content and
     * page metadata derived from the given {@link Page}.
     *
     * @param message human-readable success message
     * @param page    Spring Data page result
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> paginated(
        String message, Page<T> page
    ) {
        Map<String, Object> data = Map.of(
            "content", page.getContent(),
            "page", PageInfo.from(page)
        );
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Returns a {@code 200 OK} response containing paginated content, page
     * metadata, and the active filter criteria that produced the result.
     *
     * @param message human-readable success message
     * @param page    Spring Data page result
     * @param filters key-value map of the applied filters
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> paginatedWithFilters(
        String message, Page<T> page, Map<String, Object> filters
    ) {
        Map<String, Object> data = Map.of(
            "content", page.getContent(),
            "page", PageInfo.from(page),
            "filters", filters
        );
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Returns a {@code 200 OK} response representing an empty paginated result.
     * Used when a query produces no results but pagination metadata is still required.
     *
     * @param message  human-readable success message
     * @param pageSize the requested page size, used to populate page metadata
     */
    public static ResponseEntity<ResponseWrapper<Map<String, Object>>> emptyCollection(
        String message, int pageSize
    ) {
        Map<String, Object> data = Map.of(
            "content", List.of(),
            "page", new PageInfo(0, pageSize, 0, 0)
        );
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Returns a {@code 200 OK} response summarising the outcome of a batch operation,
     * including counts and the individual successful and failed items.
     *
     * @param message    human-readable success message
     * @param total      total number of items submitted in the batch
     * @param successful list of successfully processed items
     * @param failed     list of failure descriptors for items that could not be processed
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> batch(
        String message, int total, List<T> successful, List<Map<String, Object>> failed
    ) {
        Map<String, Object> summary = Map.of(
            "total", total,
            "successful", successful.size(),
            "failed", failed.size()
        );
        Map<String, Object> data = Map.of(
            "summary", summary,
            "successful", successful,
            "failed", failed
        );
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Returns a {@code 202 Accepted} response for an asynchronous job that has
     * been queued but not yet completed.
     *
     * @param message       human-readable success message
     * @param jobId         unique identifier of the background job
     * @param status        current job status (e.g. {@code QUEUED}, {@code PROCESSING})
     * @param estimatedTime estimated completion time in seconds, or {@code null} if unknown
     * @param statusUrl     URL the client can poll to check job progress
     */
    public static ResponseEntity<ResponseWrapper<Map<String, Object>>> async(
        String message, String jobId, String status,
        Integer estimatedTime, String statusUrl
    ) {
        Map<String, Object> data = Map.of(
            "jobId", jobId,
            "status", status,
            "estimatedTime", estimatedTime,
            "statusUrl", statusUrl
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseWrapper.accepted(message, data));
    }
}
