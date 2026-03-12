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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionCsvExportServiceImpl implements TransactionExportService {

    // CSV column headers — order must match the record writing order below
    private static final String[] HEADERS = {
        "ID", "Date", "Type", "Amount", "Category", "Payment Mode", "Note", "Created At"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT
        .builder()
        .setHeader(HEADERS)
        .setSkipHeaderRecord(false)
        .build();

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Exports a user's transactions as a UTF-8 encoded CSV file.
     */
    @Override
    @Transactional(readOnly = true)
    public void exportToStream(OutputStream outputStream) {
        User user = currentUser();

        List<Transaction> transactions = transactionRepository.findAllByUserOrderByDateDesc(user);
        log.info("Exporting {} transactions for userId: {}", transactions.size(), user.getId());

        // OutputStreamWriter bridges OutputStream → Writer with explicit charset
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

            // Flush explicitly — ensures all buffered data is written before
            // the HTTP response closes the stream
            printer.flush();

        } catch (IOException e) {
            log.error("Failed to write CSV for userId: {}", user.getId(), e);
            throw new CsvException(ErrorCode.CSV_EXPORT_FAILED);
        }
    }

    /**
     * Returns the currently authenticated user reference.
     */
    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }
}
