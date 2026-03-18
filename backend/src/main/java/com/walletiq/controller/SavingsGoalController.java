package com.walletiq.controller;

import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.savingsgoal.ContributeRequest;
import com.walletiq.dto.savingsgoal.GoalProgressResponse;
import com.walletiq.dto.savingsgoal.SavingsGoalRequest;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.SavingsGoalService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(
    name = "Savings Goals",
    description = "APIs for managing user savings goals"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class SavingsGoalController {

    private final SavingsGoalService goalService;

    @Operation(
        summary = "Create a new savings goal",
        description = "Creates a new savings goal with a target amount and deadline. Initializes with zero savings and IN_PROGRESS status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Goal created successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> create(

        @Valid @RequestBody SavingsGoalRequest request
    ) {
        return ResponseUtil.created(
            "Goal created successfully",
            goalService.create(request)
        );
    }

    @Operation(
        summary = "Get all savings goals",
        description = "Fetches all savings goals belonging to the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Goals fetched successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<GoalProgressResponse>>> getAll() {
        return ResponseUtil.ok(
            "Goals fetched successfully",
            goalService.getAll()
        );
    }

    @Operation(
        summary = "Contribute to a savings goal",
        description = "Adds a contribution to a savings goal. Marks the goal as ACHIEVED if target amount is reached."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contribution added successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid contribution amount or goal already completed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Goal not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/contribute")
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> contribute(

        @Parameter(
            description = "Unique identifier of the savings goal",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            required = true
        )
        @PathVariable UUID id,

        @Parameter(description = "Contribution request payload", required = true)
        @Valid @RequestBody ContributeRequest request
    ) {
        return ResponseUtil.ok(
            "Contribution added successfully",
            goalService.contribute(id, request)
        );
    }

    @Operation(
        summary = "Get savings goal progress",
        description = "Returns detailed progress including saved amount, remaining amount, and completion status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Goal progress fetched successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Goal not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/progress")
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> getProgress(

        @Parameter(
            description = "Unique identifier of the savings goal",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            required = true
        )
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "Goal progress fetched successfully",
            goalService.getProgress(id)
        );
    }
}