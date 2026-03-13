package com.walletiq.entity;

import com.walletiq.enums.RecurringFrequency;
import com.walletiq.enums.TxnType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "recurring_transactions",
    indexes = {
        @Index(name = "idx_recurring_user_id", columnList = "user_id"),
        @Index(name = "idx_recurring_next_execution", columnList = "next_execution_date, is_active"),
        @Index(name = "idx_recurring_user_active", columnList = "user_id, is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class RecurringTransaction extends BaseEntity {

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private TxnType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", length = 10, nullable = false)
    private RecurringFrequency frequency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // null = runs indefinitely
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Optional user note carried to generated transactions
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_mode_id")
    private PaymentMode paymentMode;
}