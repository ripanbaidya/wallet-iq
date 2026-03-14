package com.walletiq.schedular;

import com.walletiq.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoalScheduler {

    private final SavingsGoalService goalService;

    /**
     * Check for expired goals every day at 12:30 AM
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void markExpiredGoals() {
        try {
            log.info("Checking for expired savings goals...");
            goalService.markExpiredGoalsAsFailed();
        } catch (Exception e) {
            log.error("Error while checking for expired goals", e);
        }
    }
}
