package online.walletiq.service;

import online.walletiq.dto.mail.DailySummaryMailData;

public interface MailService {

    /**
     * Sends the daily financial summary email to a single user.
     */
    void sendDailySummary(DailySummaryMailData data);
}
