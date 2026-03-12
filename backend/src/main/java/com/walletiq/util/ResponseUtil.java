package com.walletiq.util;

import com.walletiq.dto.success.PageInfo;
import com.walletiq.dto.success.ResponseWrapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Utility for Building standard API response
 */
public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message) {
        return ResponseEntity.ok(ResponseWrapper.ok(message));
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message, T data) {
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(message, data));
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> accepted(String message, T data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseWrapper.accepted(message, data));
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> paginated(
        String message, Page<T> page
    ) {
        Map<String, Object> data = Map.of(
            "content", page.getContent(),
            "page", PageInfo.from(page)
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

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

    public static ResponseEntity<ResponseWrapper<Map<String, Object>>> emptyCollection(
        String message, int pageSize
    ) {
        Map<String, Object> data = Map.of(
            "content", List.of(),
            "page", new PageInfo(0, pageSize, 0, 0)
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

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
