package com.walletiq.controller;

import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.enums.Role;
import com.walletiq.service.UserService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(
    name = "Admin",
    description = "Admin related operations"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Admin access required",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
})
public class AdminController {

    private final UserService userService;

    @Operation(
        summary = "Get all users (paginated)",
        description = "Retrieves a paginated list of all users. Accessible only to ADMIN users."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Users fetched successfully",
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllUsers(
        @Parameter(hidden = true)
        Pageable pageable
    ) {
        return ResponseUtil.paginated(
            "User's Fetched Successfully",
            userService.fetchAllUsers(pageable)
        );
    }

    @Operation(
        summary = "Get user by ID",
        description = "Fetches the profile of a specific user using their unique ID. Accessible only to ADMIN users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User fetched successfully",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseWrapper<UserProfileResponse>> getUserById(
        @Parameter(description = "Unique identifier of the user", required = true)
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "User Fetched Successfully",
            userService.fetchProfileById(id)
        );
    }

    @Operation(
        summary = "Get user count by filters",
        description = "Returns total number of users filtered by role and active status. Accessible only to ADMIN users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User count fetched successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/users/count")
    public ResponseEntity<ResponseWrapper<Long>> getUserCount(
        @Parameter(description = "Role to filter users (e.g., USER, ADMIN)", required = true)
        @RequestParam Role role,

        @Parameter(description = "Filter by active status (true = active users)", required = true)
        @RequestParam boolean active
    ) {
        return ResponseUtil.ok("User Count Fetched Successfully",
            userService.countTotalUsers(role, active));
    }
}
