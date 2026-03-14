package com.walletiq.controller;

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
@Tag(name = "Savings Goals", description = "APIs for managing user savings goals")
public class SavingsGoalController {

    private final SavingsGoalService goalService;

    @Operation(
        summary = "Create a new savings goal",
        description = "Creates a new savings goal with a target amount and deadline."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Goal successfully created",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Savings goal creation request",
            required = true,
            content = @Content(schema = @Schema(implementation = SavingsGoalRequest.class))
        )
        @Valid @RequestBody SavingsGoalRequest request
    ) {
        return ResponseUtil.created("Goal created",
            goalService.create(request));
    }

    @Operation(
        summary = "Get all savings goals",
        description = "Fetches all savings goals belonging to the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Goals successfully retrieved",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<GoalProgressResponse>>> getAll() {
        return ResponseUtil.ok("Goals fetched", goalService.getAll());
    }

    @Operation(
        summary = "Contribute to a savings goal",
        description = "Adds a monetary contribution toward the specified savings goal."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contribution added successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "400", description = "Invalid contribution amount")
    })
    @PatchMapping("/{id}/contribute")
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> contribute(
        @Parameter(description = "Unique identifier of the savings goal",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable UUID id,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Contribution request payload",
            required = true,
            content = @Content(schema = @Schema(implementation = ContributeRequest.class))
        )
        @Valid @RequestBody ContributeRequest request
    ) {
        return ResponseUtil.ok("Contribution added",
            goalService.contribute(id, request));
    }

    @Operation(
        summary = "Get savings goal progress",
        description = "Returns detailed progress information for a specific savings goal."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Goal progress retrieved successfully",
            content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @GetMapping("/{id}/progress")
    public ResponseEntity<ResponseWrapper<GoalProgressResponse>> getProgress(
        @Parameter(description = "Unique identifier of the savings goal",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok("Goal progress fetched",
            goalService.getProgress(id));
    }
}