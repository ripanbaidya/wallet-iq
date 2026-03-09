package com.walletiq.backend.payload.response;

public record CategoryResponse(
    String id,
    String name,
    boolean isDefault      // true when user is null (system default)
) {
}