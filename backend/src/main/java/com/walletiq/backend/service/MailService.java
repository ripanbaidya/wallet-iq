package com.walletiq.backend.service;

import com.walletiq.backend.dto.mail.DailySummaryMailData;

public interface MailService {

    /**
     * Sends the daily financial summary email to a single user.
     */
    void sendDailySummary(DailySummaryMailData data);
}
