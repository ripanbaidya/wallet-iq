package com.walletiq.util;

/**
 * Utility class for masking sensitive user information.
 */
public final class MaskingUtil {

    private MaskingUtil() {
    }

    /**
     * Masks an email address while keeping minimal information visible.
     * Example: <br>
     * john.doe@gmail.com  ->  jo****@gmail.com <br>
     * a@gmail.com         ->  a*@gmail.com <br>
     *
     * @param email original email address
     * @return masked email
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }

        int atIndex = email.indexOf('@');
        // Invalid email format
        if (atIndex <= 0) {
            return email;
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        String maskedLocal = maskMiddle(localPart, 2, 0);

        return maskedLocal + domainPart;
    }

    // Helper methods

    /**
     * Generic reusable masking function.
     * Masks the middle portion of a string while keeping prefix and suffix
     * characters visible. <br>
     * Example:
     * maskMiddle("johnsmith", 2, 2) -> jo*****th
     *
     * @param value         original string
     * @param visiblePrefix number of characters to keep at start
     * @param visibleSuffix number of characters to keep at end
     * @return masked string
     */
    private static String maskMiddle(String value, int visiblePrefix, int visibleSuffix) {
        if (value == null || value.length() <= visiblePrefix + visibleSuffix) {
            return value;
        }

        int maskLength = value.length() - (visiblePrefix + visibleSuffix);

        String prefix = value.substring(0, visiblePrefix);
        String suffix = visibleSuffix > 0
            ? value.substring(value.length() - visibleSuffix)
            : "";

        String masked = "*".repeat(maskLength);
        return prefix + masked + suffix;
    }

}
