package online.walletiq.repository;

import online.walletiq.entity.EmailVerificationOtp;
import online.walletiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, UUID> {

    /**
     * Find the latest unused, unexpired OTP for a user.
     */
    Optional<EmailVerificationOtp> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    /**
     * Invalidate all existing OTPs for a user before issuing a new one.
     */
    @Modifying
    @Query("update EmailVerificationOtp ev set ev.used = true where ev.user = :user and ev.used = false")
    void invalidateAllOtpForUser(@Param("user") User user);

    /**
     * Deletes all expired or used OTPs.
     */
    @Modifying
    @Query("delete from EmailVerificationOtp ev where ev.used = true or ev.expiresAt < :now")
    int deleteAllExpiredOrUsed(@Param("now") Instant now);
}
