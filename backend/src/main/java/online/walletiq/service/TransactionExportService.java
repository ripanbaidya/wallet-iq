package online.walletiq.service;

import java.io.OutputStream;

public interface TransactionExportService {

    /**
     * Writes all transactions for the current user into the provided OutputStream
     * as a UTF-8 encoded CSV file.
     *
     * @param outputStream the stream to write CSV bytes into (e.g. HTTP response stream)
     */
    void exportToStream(OutputStream outputStream);
}
