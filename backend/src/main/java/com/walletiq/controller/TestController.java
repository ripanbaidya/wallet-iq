package com.walletiq.controller;

import com.walletiq.dto.test.TestRequest;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.dto.test.TestResponse;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@Tag(name = "Test")
public class TestController {

    @GetMapping("/success")
    @Operation(summary = "Test 200 Success Response")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<@NonNull ResponseWrapper<TestResponse>> testSuccess() {
        var data = TestResponse.of(UUID.randomUUID().toString(), "John Doe", "john@examle.com");
        return ResponseUtil.ok("Success response test", data);
    }

    @PostMapping("/created")
    @Operation(summary = "Test 201 Created response")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<@NonNull ResponseWrapper<TestResponse>> testCreated(
        @Valid @RequestBody TestRequest request
    ) {
        var data = TestResponse.of(UUID.randomUUID().toString(), request.name(), request.email());
        return ResponseUtil.ok("Test Validation", data);
    }

    @DeleteMapping("/no-content")
    @Operation(summary = "Test 204 No Content response")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<@NonNull Void> testNoContent() {
        return ResponseUtil.noContent();
    }

    // Pagination

    @GetMapping("/paginated")
    @Operation(summary = "Test paginated response")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<@NonNull ResponseWrapper<Map<String, Object>>> testPaginated(Pageable pageable) {
        List<TestResponse> content = List.of(
            new TestResponse("1", "User One", "user1@example.com"),
            new TestResponse("2", "User Two", "user2@example.com"),
            new TestResponse("3", "User Three", "user3@example.com")
        );
        Page<@NonNull TestResponse> page = new PageImpl<>(content, pageable, 100);
        return ResponseUtil.paginated("Data retrieved successfully", page);
    }

    @GetMapping("/paginated/empty")
    @Operation(summary = "Test empty paginated response")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<@NonNull ResponseWrapper<Map<String, Object>>> testEmptyPaginated() {
        return ResponseUtil.emptyCollection("No data found", 10);
    }

    @GetMapping("/paginated/filtered")
    @Operation(summary = "Test paginated response with filters")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<@NonNull ResponseWrapper<Map<String, Object>>> testFilteredPaginated(
        @RequestParam(required = false) String status,
        Pageable pageable
    ) {
        List<TestResponse> content = List.of(
            new TestResponse("1", "Active User", "active@example.com"));
        Page<@NonNull TestResponse> page = new PageImpl<>(content, pageable, 1);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", status != null ? status : "all");

        return ResponseUtil.paginatedWithFilters("Filtered data retrieved", page, filters);
    }
}
