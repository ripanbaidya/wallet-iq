package com.walletiq.backend.dto.categories;

public record CategoryResponse(
    String id,
    String name,
    boolean isDefault      // true when user is null (system default)
) {
}