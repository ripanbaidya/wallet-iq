package com.walletiq.enums;

/**
 * Represents the role assigned to a user in the system.<br>
 * The {@code getAuthority()} method converts the role into the format
 * expected by Spring Security (e.g., ROLE_ADMIN, ROLE_USER).
 */
public enum Role {

    /**
     * System administrator with elevated privileges
     */
    ADMIN,

    /**
     * Regular application user
     */
    USER;

    /**
     * Returns the Spring Security authority string for this role.
     *
     * @return authority in the format ROLE_{ROLE_NAME}
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}