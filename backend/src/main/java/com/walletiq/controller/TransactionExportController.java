package com.walletiq.controller;

import com.walletiq.service.TransactionExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Provides transaction data export endpoints.
 */
@RestController
@RequestMapping("/transactions/export")
@RequiredArgsConstructor
@Tag(name = "Transaction Export", description = "Export transaction data")
public class TransactionExportController {

    private final TransactionExportService transactionExportService;

    @Operation(
        summary = "Export transactions as CSV",
        description = "Downloads all transactions for the authenticated user as a CSV file."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file downloaded successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Export failed")
    })
    @GetMapping("/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        String filename = "walletiq-transactions-" + LocalDate.now() + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // BOM ensures Excel opens the UTF-8 file correctly on Windows
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + filename + "\""
        );

        transactionExportService.exportToStream(response.getOutputStream());
    }
}