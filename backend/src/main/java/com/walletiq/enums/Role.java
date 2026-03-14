package com.walletiq.enums;

public enum Role {
    ADMIN,
    USER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
