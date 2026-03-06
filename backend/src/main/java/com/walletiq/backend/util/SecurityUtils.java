package com.walletiq.backend.util;

import com.walletiq.backend.entity.UserPrincipal;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.AuthException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }


    /**
     * Returns the currently authenticated user principal.
     *
     * @throws AuthException if the context is empty or unauthenticated.
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(ErrorCode.UNAUTHENTICATED);
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AuthException(ErrorCode.UNAUTHENTICATED);
        }

        return principal;
    }

    /**
     * Returns the current user's ID directly.
     */
    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Returns the current user's email directly.
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
