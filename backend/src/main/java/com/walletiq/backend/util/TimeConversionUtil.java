package com.walletiq.backend.util;

import java.time.Duration;

/**
 * Utility class for time unit conversions.
 */
public final class TimeConversionUtil {

    private TimeConversionUtil() {
    }

    /**
     * Converts milliseconds to seconds.
     *
     * @param millis time duration in milliseconds
     * @return equivalent duration in seconds
     */
    public static long millisToSeconds(long millis) {
        return Duration.ofMillis(millis).toSeconds();
    }
}
