package online.walletiq.service.impl;

import online.walletiq.dto.mail.DailySummaryMailData;
import online.walletiq.service.MailService;
import online.walletiq.util.MaskingUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Handles all outbound email sending for WalletIQ.
 * Uses Thymeleaf to render HTML templates before dispatch.
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    public MailServiceImpl(JavaMailSender javaMailSender,
                           @Qualifier("emailTemplateEngine") SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendDailySummary(DailySummaryMailData data) {
        String maskedEmail = MaskingUtil.maskEmail(data.recipientEmail());

        try {
            String html = renderTemplate("daily-summary", buildContext(data));
            send(
                data.recipientEmail(),
                "💰 Your Daily Finance Summary — " + data.date(),
                html,
                data.csvAttachment(),
                data.csvFileName()
            );
            log.debug("Daily Summary Mail Sent to: {}", maskedEmail);
        } catch (Exception e) {
            log.error("Failed to send daily summary email to {}", maskedEmail, e);
        }
    }

    private Context buildContext(DailySummaryMailData data) {
        Context context = new Context();
        context.setVariables(Map.of(
            "name", data.recipientName(),
            "date", data.date(),
            "totalIncome", data.totalIncome(),
            "totalExpenses", data.totalExpenses(),
            "netBalance", data.netBalance(),
            "transactionCount", data.transactionCount()
        ));
        return context;
    }

    private String renderTemplate(String templateName, Context context) {
        return templateEngine.process(templateName, context);
    }

    private void send(String to, String subject, String html,
                      byte[] attachmentBytes, String attachmentFileName)
        throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);   // true = isHtml

        if (attachmentBytes != null && attachmentBytes.length > 0) {
            helper.addAttachment(
                attachmentFileName,
                new ByteArrayResource(attachmentBytes),
                "text/csv"
            );
        }

        javaMailSender.send(message);
    }
}
