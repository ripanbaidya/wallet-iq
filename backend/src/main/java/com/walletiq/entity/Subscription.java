package com.walletiq.entity;

import com.walletiq.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
    name = "subscriptions",
    indexes = @Index(name = "idx_subscription_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Subscription extends BaseEntity {

    // Razorpay order ID — created when user initiates payment
    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    // Razorpay payment ID — filled after successful payment verification
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    // Razorpay signature — filled after successful payment verification
    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Column(name = "amount", nullable = false)
    private int amount;  // in INR

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Domain behaviour

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE
            && expiresAt != null
            && Instant.now().isBefore(expiresAt);
    }
}