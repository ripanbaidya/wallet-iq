package com.walletiq.backend.schedular;

import com.walletiq.backend.entity.User;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.service.MailService;
import com.walletiq.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scheduler responsible for sending daily financial summary emails to all
 * active users at 9:00 PM every day.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailySummaryScheduler {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    private final UserRepository userRepository;
    private final MailService mailService;
    private final TransactionService transactionService;

    /**
     * Fetches all active users and sends each a daily summary email.
     */
    // @Scheduled(cron = "*/30 * * * * *") // every 30s
    @Scheduled(cron = "0 0 21 * * *") // daily at 9.00 PM
    public void sendDailySummaries() {
        List<User> activeUsers = userRepository.findAllActiveUsers();
        log.info("Sending Daily summaries to {} users", activeUsers.size());

        int success = 0;
        int failed = 0;

        for (User user : activeUsers) {
            try {
                var data = transactionService.buildDailySummaryMailData(user);
                mailService.sendDailySummary(data);
                success++;
            } catch (Exception e) {
                log.error("Failed to build/send summary for userId={}", user.getId(), e);
                failed++;
            }
        }

        log.info("Daily summary sending completed. Success: {}, Failed: {}", success, failed);
    }
}