package com.walletiq.service.impl;

import com.walletiq.entity.Transaction;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.CsvException;
import com.walletiq.repository.TransactionRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.TransactionExportService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionCsvExportServiceImpl implements TransactionExportService {

    // CSV column headers — order must match the record writing order below
    private static final String[] HEADERS = {
        "ID", "Date", "Type", "Amount", "Category", "Payment Mode", "Note", "Created At"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader(HEADERS)
        .setSkipHeaderRecord(false)
        .build();

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * HTTP export — streams all transactions for the current
     * user to the response OutputStream.
     */
    @Override
    @Transactional(readOnly = true)
    public void exportToStream(OutputStream outputStream) {
        User user = currentUser();
        List<Transaction> transactions = transactionRepository.findAllByUserOrderByDateDesc(user);

        log.info("Exporting {} transactions for userId: {}", transactions.size(), user.getId());
        writeCsv(transactions, outputStream);
    }

    /**
     * Builds a CSV byte array for a specific user and date.
     * Intended for scheduled jobs (e.g. email attachments) where no HTTP
     * response is present.
     *
     * @param user the target user
     * @param date the date to filter transactions by
     * @return UTF-8 encoded CSV bytes; empty CSV with headers if no transactions exist
     */
    @Transactional(readOnly = true)
    public byte[] exportDailyAsBytes(User user, LocalDate date) {
        List<Transaction> transactions = transactionRepository
            .findAllByUserAndDateOrderByDateDesc(user, date);

        log.debug("Building daily CSV for userId: {}, date: {}, count: {}",
            user.getId(), date, transactions.size());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeCsv(transactions, baos);

        return baos.toByteArray();
    }

    /* ----------- Helper methods ----------- */

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    /**
     * CSV writing logic
     */
    private void writeCsv(List<Transaction> transactions, OutputStream outputStream) {
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {

            for (Transaction txn : transactions) {
                printer.printRecord(
                    txn.getId(),
                    txn.getDate(),
                    txn.getType().name(),
                    txn.getAmount(),
                    txn.getCategory() != null ? txn.getCategory().getName() : "",
                    txn.getPaymentMode() != null ? txn.getPaymentMode().getName() : "",
                    txn.getNote() != null ? txn.getNote() : "",
                    txn.getCreatedAt()
                );
            }
            printer.flush();

        } catch (IOException e) {
            log.error("Failed to write CSV", e);
            throw new CsvException(ErrorCode.CSV_EXPORT_FAILED);
        }
    }
}
