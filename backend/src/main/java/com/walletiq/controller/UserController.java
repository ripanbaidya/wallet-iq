package com.walletiq.controller;

import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.dto.user.UpdateProfileRequest;
import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.service.UserService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(
    name = "Users",
    description = "APIs for managing user profile operations"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
    @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
})
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get current user profile",
        description = "Fetches the profile details of the currently authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User profile fetched successfully",
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<UserProfileResponse>> getProfile() {
        return ResponseUtil.ok(
            "User profile fetched successfully",
            userService.fetchProfile()
        );
    }

    @Operation(
        summary = "Update current user profile",
        description = "Updates the full name of the currently authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @PatchMapping("/me")
    public ResponseEntity<ResponseWrapper<UserProfileResponse>> updateProfile(
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseUtil.ok("User profile updated successfully",
            userService.updateProfile(request.fullName()));
    }
}
