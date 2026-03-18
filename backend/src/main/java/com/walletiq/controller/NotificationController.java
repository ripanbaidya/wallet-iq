package com.walletiq.controller;

import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.notification.NotificationResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.NotificationService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(
    name = "Notifications",
    description = "APIs for managing user notifications"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "Get all notifications",
        description = "Fetches all notifications for the authenticated user, ordered by latest first."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notifications fetched successfully",
            content = @Content(schema = @Schema(implementation = NotificationResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<NotificationResponse>>> getAll() {
        return ResponseUtil.ok(
            "Notifications fetched successfully",
            notificationService.getAll()
        );
    }

    @Operation(
        summary = "Delete notification by ID",
        description = "Deletes a specific notification belonging to the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notification deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteById(

        @Parameter(description = "Unique identifier of the notification", required = true)
        @PathVariable UUID id
    ) {
        notificationService.deleteById(id);
        return ResponseUtil.ok("Notification deleted successfully");
    }

    @Operation(
        summary = "Delete all notifications",
        description = "Deletes all notifications for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "All notifications deleted successfully"
        )
    })
    @DeleteMapping
    public ResponseEntity<ResponseWrapper<Void>> deleteAll() {
        notificationService.deleteAll();
        return ResponseUtil.ok("All notifications deleted successfully");
    }
}