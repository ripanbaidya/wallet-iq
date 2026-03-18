package com.walletiq.controller;

import com.walletiq.dto.dashboard.DashboardResponse;
import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.DashboardService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(
    name = "Dashboard",
    description = "APIs for fetching user financial dashboard insights"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
        summary = "Get dashboard data",
        description = "Returns aggregated financial insights including income, expenses, category breakdown, trends, and top expenses for a given month."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dashboard fetched successfully",
            content = @Content(schema = @Schema(implementation = DashboardResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid month format (expected yyyy-MM)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<DashboardResponse>> getDashboard(

        @Parameter(
            description = "Month in format yyyy-MM (defaults to current month if not provided)",
            example = "2026-03"
        )
        @RequestParam(required = false) String month
    ) {
        YearMonth yearMonth = (month != null)
            ? YearMonth.parse(month)
            : YearMonth.now();

        return ResponseUtil.ok(
            "Dashboard information fetched successfully",
            dashboardService.getDashboard(yearMonth)
        );
    }
}