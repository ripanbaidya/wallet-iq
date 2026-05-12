package online.walletiq.util;

import online.walletiq.constant.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.UUID;

/**
 * Provides programmatic cache eviction for cases where the cache key
 * cannot be determined via @CacheEvict annotations alone —
 * specifically when the key depends on data fetched within the method body
 * (e.g. deleteTransaction needs the transaction's date before it can evict).
 */
@Component
@RequiredArgsConstructor
public class CacheEvictionHelper {

    private final CacheManager cacheManager;

    /**
     * Evicts the dashboard cache entry for a specific user and month.
     *
     * @param userId the owner of the dashboard cache entry
     * @param month  the month whose dashboard snapshot should be invalidated
     */
    public void evictDashboard(UUID userId, YearMonth month) {
        var cache = cacheManager.getCache(CacheNames.DASHBOARD);
        if (cache != null) {
            cache.evict(userId.toString() + ":" + month.toString());
        }
    }
}