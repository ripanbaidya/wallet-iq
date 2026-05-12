package online.walletiq.repository;

import online.walletiq.dto.dashboard.CategoryBreakdownItem;
import online.walletiq.dto.dashboard.DailyTrendItem;
import online.walletiq.dto.dashboard.TopExpenseItem;
import online.walletiq.entity.Transaction;
import online.walletiq.entity.User;
import online.walletiq.enums.TxnType;
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

    List<Transaction> findAllByUserAndDateOrderByDateDesc(User user, LocalDate date);

    @Query("""
            select COALESCE(sum(t.amount), 0)
            from Transaction t
            where t.user.id     = :userId
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

    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.user.id = :userId
          and t.type = :type
          and t.date between :from and :to
        """)
    BigDecimal sumByUserAndTypeAndDateBetween(
        @Param("userId") UUID userId,
        @Param("type") TxnType type,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    @Query("""
        select new com.walletiq.dto.dashboard.CategoryBreakdownItem(
            c.name,
            sum(t.amount)
        )
        from Transaction t
        JOIN t.category c
        where t.user.id = :userId
          AND t.type = :type
          AND t.date BETWEEN :from AND :to
        GROUP BY c.name
        order by sum(t.amount) desc
        """)
    List<CategoryBreakdownItem> findCategoryBreakdown(
        @Param("userId") UUID userId,
        @Param("type") TxnType type,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    @Query("""
        select new com.walletiq.dto.dashboard.DailyTrendItem(
            t.date,
            sum(case when t.type = com.walletiq.enums.TxnType.INCOME  then t.amount else 0 end),
            sum(case when t.type = com.walletiq.enums.TxnType.EXPENSE then t.amount else 0 end)
        )
        from Transaction t
        where t.user.id = :userId
          AND t.date BETWEEN :from AND :to
        GROUP BY t.date
        order by t.date ASC
        """)
    List<DailyTrendItem> findDailyTrend(
        @Param("userId") UUID userId,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    @Query("""
        SELECT new com.walletiq.dto.dashboard.TopExpenseItem(
            t.id,
            t.amount,
            c.name,
            t.note,
            t.date
        )
        FROM Transaction t
        LEFT JOIN t.category c
        WHERE t.user.id = :userId
          AND t.type = com.walletiq.enums.TxnType.EXPENSE
          AND t.date BETWEEN :from AND :to
        ORDER BY t.amount DESC
        LIMIT 5
        """)
    List<TopExpenseItem> findTop5Expenses(
        @Param("userId") UUID userId,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );
}