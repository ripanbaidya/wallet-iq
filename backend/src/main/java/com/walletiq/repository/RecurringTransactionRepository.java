package com.walletiq.repository;

import com.walletiq.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {

    /**
     * User by schedular, fetch all due active entities
     */
    @Query("""
            select r from RecurringTransaction r
            where r.isActive = true
            and r.nextExecutionDate <= :today
            and (r.endDate is null or r.endDate >= :today)
        """)
    List<RecurringTransaction> findDueTransactions(@Param("today") LocalDate today);

    /**
     * Find all active recurring transactions for a user
     */
    List<RecurringTransaction> findByUser_IdAndIsActiveTrue(UUID userId);

    /**
     * All recurring for a user (including inactive)
     */
    List<RecurringTransaction> findByUser_Id(UUID userId);

    /**
     * Find a recurring transaction by ID and User ID
     */
    Optional<RecurringTransaction> findByIdAndUser_Id(UUID id, UUID userId);

    @Query("""
            select r from RecurringTransaction r
            where r.user.id = :userId
            and r.isActive = true
            and r.startDate <= :forecastUntil
            and r.nextExecutionDate <= :forecastUntil
            and (r.endDate is null or r.endDate >= :today)
        """)
    List<RecurringTransaction> findForecastableByUser(
        @Param("userId") UUID userId,
        @Param("today") LocalDate today,
        @Param("forecastUntil") LocalDate forecastUntil
    );

}
