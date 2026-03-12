package com.walletiq.dto.paymentmode;

public record PaymentModeResponse(
    String id,
    String name,
    boolean isDefault       // true when user is null (system default)
) {
}