package online.walletiq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "payment_modes",
    indexes = @Index(name = "idx_payment_modes_user_id", columnList = "user_id"),
    uniqueConstraints = @UniqueConstraint(
        name = "uq_payment_mode_name_user",
        columnNames = {"name", "user_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class PaymentMode extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // NULL = system default (visible to all users)
    // NOT NULL = belongs to a specific user (private)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}