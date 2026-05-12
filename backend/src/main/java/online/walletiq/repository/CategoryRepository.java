package online.walletiq.repository;

import online.walletiq.entity.Category;
import online.walletiq.entity.User;
import online.walletiq.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Retrieves all categories visible to a user, including system defaults
     * (where the user is null) and the user's own categories, filtered by type.
     */
    @Query("""
            select c from Category c
            where c.categoryType = :type
            and (c.user is null or c.user = :user)
            order by c.name asc
        """)
    List<Category> findAllVisibleToUser(@Param("user") User user,
                                        @Param("type") CategoryType type);

    /**
     * Checks if a category with the given name already exists for the user.
     * Used to prevent duplicate category names.
     *
     * @param name the category name (case-insensitive)
     * @param user the user to check for
     * @return true if a duplicate exists, false otherwise
     */
    boolean existsByNameIgnoreCaseAndUserAndCategoryType(String name, User user, CategoryType categoryType);

    /**
     * Finds a category by ID that belongs to the specified user.
     * Ensures users can only access their own categories.
     *
     * @param id   the category ID
     * @param user the owner user
     * @return Optional containing the category if found and owned by a user
     */
    Optional<Category> findByIdAndUser(UUID id, User user);
}
