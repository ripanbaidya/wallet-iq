package com.walletiq.schedular;

import com.walletiq.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionScheduler {

    private final RecurringTransactionService recurringTransactionService;

    /**
     * Runs daily at 08:00 AM server time.
     * Processes all recurring transactions that are due or overdue.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void processDueRecurringTransactions() {

        long startTime = System.currentTimeMillis();

        log.info("RecurringTransactionScheduler started");

        try {
            recurringTransactionService.processDueRecurringTransactions();
            long duration = System.currentTimeMillis() - startTime;

            log.info("RecurringTransactionScheduler completed successfully in {} ms", duration);

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;

            log.error("RecurringTransactionScheduler failed after {} ms. Error: {}",
                duration, ex.getMessage(), ex
            );
        }
    }
}