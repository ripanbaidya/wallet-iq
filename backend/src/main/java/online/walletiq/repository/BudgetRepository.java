package online.walletiq.repository;

import online.walletiq.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    /**
     * Returns all budgets for a user in the given month.
     *
     * @param userId the user's ID
     * @param month  the target month
     * @return list of {@link Budget} entities for that month
     */
    List<Budget> findByUser_IdAndMonth(UUID userId, YearMonth month);

    /**
     * Finds a budget by its ID, scoped to the given user.
     * Returns empty if the budget doesn't exist or doesn't belong to the user.
     *
     * @param id     the budget ID
     * @param userId the user's ID
     * @return an {@link Optional} containing the matching budget, or empty if not found
     */
    Optional<Budget> findByIdAndUser_Id(UUID id, UUID userId);

    /**
     * Checks whether a budget already exists for the given user, category, and month.
     * Used to prevent duplicate budget entries.
     *
     * @param userId     the user's ID
     * @param categoryId the category ID
     * @param month      the target month
     * @return {@code true} if a budget exists, {@code false} otherwise
     */
    boolean existsByUser_IdAndCategoryIdAndMonth(UUID userId, UUID categoryId, YearMonth month);

    /**
     * Check budget by user, category and month.
     *
     * @param userId     the user's ID
     * @param categoryId the category ID
     * @param month      the target month
     * @return {@code Budget}
     */
    Optional<Budget> findByUser_IdAndCategoryIdAndMonth(UUID userId, UUID categoryId, YearMonth month);

}
