package online.walletiq.service.impl;

import online.walletiq.entity.EmailVerificationOtp;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.exception.OtpException;
import online.walletiq.exception.UserException;
import online.walletiq.repository.EmailVerificationOtpRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.EmailVerificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

import static online.walletiq.util.MaskingUtil.maskEmail;

@Service
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final EmailVerificationOtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public EmailVerificationServiceImpl(UserRepository userRepository,
                                        EmailVerificationOtpRepository otpRepository,
                                        JavaMailSender mailSender,
                                        @Qualifier("emailTemplateEngine") SpringTemplateEngine templateEngine) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    // Public method Implementations

    @Override
    @Transactional
    public void sendOtp(String email) {
        User user = findUserByEmail(email);

        // Check if email is already verified
        if (user.isEmailVerified()) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Invalidate all otp's before issuing new otp
        otpRepository.invalidateAllOtpForUser(user);

        String generatedOtp = generateOtp();
        saveOtp(user, generatedOtp);
        dispatchEmail(user.getFullName(), email, generatedOtp);

        log.info("Otp sent to email {}", maskEmail(email));
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        User user = findUserByEmail(email);

        // Check if email is already verified
        if (user.isEmailVerified()) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Find the latest unused OTP for the user
        var record = otpRepository.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)
            .orElseThrow(() -> new OtpException(ErrorCode.OTP_INVALID,
                String.format("Otp for email %s not found", maskEmail(email))
            ));

        // Validate the OTP
        if (record.isExpired()) {
            throw new OtpException(ErrorCode.OTP_EXPIRED);
        }

        if (!record.getOtp().equals(otp)) {
            throw new OtpException(ErrorCode.OTP_INVALID,
                "Otp does not match"
            );
        }

        // Mark the OTP as used, verify the user's email address, and activate the user
        record.markUsed();
        user.setEmailVerified(true);
        user.setActive(true);

        otpRepository.save(record);
        userRepository.save(user);

        log.info("Email verified successfully for email={}", maskEmail(email));
    }

    // Private helper methods

    /**
     * Find user by his email
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND,
                    String.format("User not found with email: %s", maskEmail(email))
                )
            );
    }

    /**
     * Generate a 6-digit OTP
     */
    private String generateOtp() {
        int code = 100_000 + SECURE_RANDOM.nextInt(900_000);
        return String.valueOf(code);
    }

    /**
     * Save the OTP in the database
     */
    private void saveOtp(User user, String otpCode) {
        EmailVerificationOtp otp = new EmailVerificationOtp();

        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plusSeconds(otpExpiryMinutes * 60L);

        otp.setUser(user);
        otp.setOtp(otpCode);
        otp.setCreatedAt(createdAt);
        otp.setExpiresAt(expiresAt);

        // Save the otp into database
        otpRepository.save(otp);
    }

    /**
     * Send the OTP via email
     */
    private void dispatchEmail(String name, String email, String otpCode) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                "name", name,
                "otp", otpCode,
                "expiryMinutes", otpExpiryMinutes
            ));

            String html = templateEngine.process("email-verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(email);
            helper.setSubject("🔐 Your WalletIQ Verification Code");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to={}", maskEmail(email), e);
            throw new OtpException(ErrorCode.OTP_SEND_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error sending OTP to={}", maskEmail(email), e);
            throw new OtpException(ErrorCode.OTP_SEND_FAILED);
        }
    }
}
