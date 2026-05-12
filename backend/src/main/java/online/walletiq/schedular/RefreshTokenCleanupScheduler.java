package online.walletiq.schedular;

import online.walletiq.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Deletes all revoked and expired refresh tokens from the database.
     */
    // @Scheduled(cron = "0 */1 * * * *") // every 1m
    @Scheduled(cron = "0 0 2 * * *") // daily at 2 AM
    @Transactional
    public void cleanupInactiveToken() {
        log.info("Starting cleanup of inactive refresh tokens");

        try {
            int deletedCount = refreshTokenRepository.deleteAllInactiveTokens(Instant.now());
            log.info("Successfully cleaned up {} inactive refresh tokens", deletedCount);
        } catch (Exception e) {
            log.error("Error occurred while cleaning up inactive refresh tokens", e);
        }
    }
}
