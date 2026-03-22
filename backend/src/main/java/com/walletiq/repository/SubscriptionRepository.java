package com.walletiq.repository;

import com.walletiq.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Subscription} entities.
 * Provides derived and JPQL queries for subscription lifecycle management,
 * including active subscription lookup and expiry detection for the scheduler.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * Finds a subscription by its Razorpay order ID.
     * Used during payment verification to locate the corresponding subscription record.
     *
     * @param orderId the Razorpay order ID
     * @return the matching subscription, or empty if not found
     */
    Optional<Subscription> findByRazorpayOrderId(String orderId);

    /**
     * Returns the most recently expiring active subscription for the given user.
     * A subscription is considered active if its status is {@code ACTIVE}
     * and its expiry timestamp is in the future relative to {@code now}.
     *
     * @param userId the user's ID
     * @param now    the current timestamp used as the expiry boundary
     * @return the latest active subscription, or empty if none exists
     */
    @Query("""
        select s from Subscription s
        where s.user.id = :userId
          and s.status = com.walletiq.enums.SubscriptionStatus.ACTIVE
          and s.expiresAt > :now
        order by s.expiresAt desc
        """)
    Optional<Subscription> findActiveByUserId(
        @Param("userId") UUID userId,
        @Param("now") Instant now
    );

    /**
     * Returns all subscriptions that are still marked {@code ACTIVE} but have
     * already passed their expiry timestamp.
     * Consumed by the scheduler to bulk-transition stale subscriptions to {@code EXPIRED}.
     *
     * @param now the current timestamp used as the expiry boundary
     * @return list of expired-but-unprocessed subscriptions, or empty list if none
     */
    @Query("""
        select s from Subscription s
        where s.status = com.walletiq.enums.SubscriptionStatus.ACTIVE
          and s.expiresAt < :now
        """)
    List<Subscription> findAllExpired(@Param("now") Instant now);
}
