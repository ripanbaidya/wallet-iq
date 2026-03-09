package com.walletiq.backend.repository;

import com.walletiq.backend.entity.Transaction;
import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.TxnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    @Query("""
            select t from Transaction t
            left join fetch t.category c
            left join fetch t.paymentMode pm
            where t.user = :user
              and (:type is null or t.type = :type)
              and (:categoryId is null or c.id = :categoryId)
              and t.date >= coalesce(:dateFrom, t.date)
              and t.date <= coalesce(:dateTo, t.date)
            order by t.date desc, t.createdAt desc
        """)
    List<Transaction> findAllByFilter(
        @Param("user") User user,
        @Param("type") TxnType type,
        @Param("categoryId") UUID categoryId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );

    /**
     * Returns a transaction by id for the given user.
     * join fetch loads category and paymentMode in the same query.
     */
    @Query("""
        select t from Transaction t
        join fetch t.category
        join fetch t.paymentMode
        where t.id = :id and t.user = :user
        """)
    Optional<Transaction> findByIdAndUser(
        @Param("id") UUID id,
        @Param("user") User user
    );
}