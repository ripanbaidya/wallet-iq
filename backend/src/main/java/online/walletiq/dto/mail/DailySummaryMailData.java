package online.walletiq.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Data used to render the daily financial summary email")
public record DailySummaryMailData(

    @Schema(
        description = "Recipient's full name",
        example = "Ripan Baidya"
    )
    String recipientName,

    @Schema(
        description = "Recipient's email address",
        example = "ripan@gmail.com"
    )
    String recipientEmail,

    @Schema(
        description = "Formatted date representing the summary day",
        example = "Monday, 12 March 2026"
    )
    String date,

    @Schema(
        description = "Total income recorded on the given day",
        example = "3500.00"
    )
    BigDecimal totalIncome,

    @Schema(
        description = "Total expenses recorded on the given day",
        example = "2100.50"
    )
    BigDecimal totalExpenses,

    @Schema(
        description = "Net balance calculated as total income minus total expenses",
        example = "1399.50"
    )
    BigDecimal netBalance,

    @Schema(
        description = "Total number of transactions recorded on the given day",
        example = "7"
    )
    int transactionCount,

    @Schema(
        description = "CSV Attachment"
    )
    byte[] csvAttachment,

    @Schema(
        description = "Name of the CSV file attachment",
        example = "transactions_2026-03-12.csv"
    )
    String csvFileName

) {
}