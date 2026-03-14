package com.walletiq.repository;

import com.walletiq.entity.Transaction;
import com.walletiq.entity.User;
import com.walletiq.enums.TxnType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Returns transactions for a user with optional filters.
     * If any parameter is null, that filter is ignored.
     * Uses join fetch to load category and paymentMode eagerly to avoid the
     * N+1 select problem.
     */
    @Query(
        value = """
            select t from Transaction t
            left join fetch t.category c
            left join fetch t.paymentMode pm
            where t.user = :user
              and (:type is null or t.type = :type)
              and (:categoryId is null or c.id = :categoryId)
              and t.date >= coalesce(:dateFrom, t.date)
              and t.date <= coalesce(:dateTo, t.date)
            """,
        countQuery = """
            select count(t) from Transaction t
            left join t.category c
            where t.user = :user
              and (:type is null or t.type = :type)
              and (:categoryId is null or c.id = :categoryId)
              and t.date >= coalesce(:dateFrom, t.date)
              and t.date <= coalesce(:dateTo, t.date)
            """
    )
    Page<Transaction> findAllByFilter(
        @Param("user") User user,
        @Param("type") TxnType type,
        @Param("categoryId") UUID categoryId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable
    );

    Optional<Transaction> findByIdAndUser_Id(UUID id, UUID userId);

    List<Transaction> findByUserAndDate(User user, LocalDate date);

    List<Transaction> findAllByUserOrderByDateDesc(User user);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id     = :userId
              AND t.category.id = :categoryId
              AND t.type        = com.walletiq.enums.TxnType.EXPENSE
              AND t.date >= :startDate
              AND t.date <= :endDate
        """)
    BigDecimal sumExpensesByCategoryAndMonth(
        @Param("userId") UUID userId,
        @Param("categoryId") UUID categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}