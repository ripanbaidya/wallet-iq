package com.walletiq.backend.service;

public interface EmailVerificationService {

    void sendOtp(String email);

    void verifyOtp(String email, String otp);
}