package online.walletiq.schedular;

import online.walletiq.dto.mail.DailySummaryMailData;
import online.walletiq.entity.Transaction;
import online.walletiq.entity.User;
import online.walletiq.enums.Role;
import online.walletiq.enums.TxnType;
import online.walletiq.repository.TransactionRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.MailService;
import online.walletiq.service.impl.TransactionCsvExportServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scheduler responsible for sending daily financial summary emails to all
 * active users.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailySummaryScheduler {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    private final MailService mailService;
    private final TransactionCsvExportServiceImpl csvExportService;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Fetches all active users and sends each a daily summary email.
     * <br>Runs daily at <b>9:00 PM</b>
     */
    // @Scheduled(cron = "0 0 21 * * *")
    public void sendDailySummaries() {
        LocalDate today = LocalDate.now();
        log.info("Running daily summary scheduler for date: {}", today);

        // We will only send mails to active users (Admin is not included)
        List<User> activeUsers = userRepository.findAllActiveUsersExceptRoleAdmin(Role.ADMIN);

        int success = 0;
        int failed = 0;

        for (User user : activeUsers) {
            try {
                buildDailySummaryMailData(user, today);
                success++;
            } catch (Exception e) {
                log.error("Failed to build/send summary for userId={}", user.getId(), e);
                failed++;
            }
        }

        log.info("Daily summary sending completed. Success: {}, Failed: {}", success, failed);
    }

    public void buildDailySummaryMailData(User user, LocalDate today) {
        List<Transaction> todayTransactions = transactionRepository
            .findByUserAndDate(user, today);

        // Calculate the total Income and Expenses
        BigDecimal totalIncome = todayTransactions.stream()
            .filter(t -> t.getType() == TxnType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = todayTransactions.stream()
            .filter(t -> t.getType() == TxnType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build the CSV attachment
        byte[] csvBytes = csvExportService.exportDailyAsBytes(user, today);
        String csvFileName = "walletiq-transactions-" + today + ".csv";

        var mailData = new DailySummaryMailData(
            user.getFullName(),
            user.getEmail(),
            today.format(DATE_FORMAT),
            totalIncome,
            totalExpense,
            totalIncome.subtract(totalExpense),
            todayTransactions.size(),
            csvBytes,
            csvFileName
        );

        mailService.sendDailySummary(mailData);
    }
}