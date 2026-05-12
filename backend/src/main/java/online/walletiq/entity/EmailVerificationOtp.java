package online.walletiq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "email_verification_otp",
    indexes = {
        // Fast lookup when verifying OTP
        @Index(name = "idx_email_otp_user_otp_used", columnList = "user_id, otp, used"),

        // Fetch latest OTP for a user
        @Index(name = "idx_email_otp_user_created_at", columnList = "user_id, created_at"),

        // Cleanup expired OTPs
        @Index(name = "idx_email_otp_expires_at", columnList = "expires_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private boolean used = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Domain behaviour

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !this.used && !isExpired();
    }

    public void markUsed() {
        this.used = true;
    }
}