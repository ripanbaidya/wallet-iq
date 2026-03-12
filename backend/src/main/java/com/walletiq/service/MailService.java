package com.walletiq.service;

import com.walletiq.dto.mail.DailySummaryMailData;

public interface MailService {

    /**
     * Sends the daily financial summary email to a single user.
     */
    void sendDailySummary(DailySummaryMailData data);
}
