package com.walletiq.backend.repository;

import com.walletiq.backend.entity.Category;
import com.walletiq.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Fetch system defaults (user IS NULL) + user's own categories
    @Query("select c from Category c where c.user is null or c.user = :user order by c.name asc")
    List<Category> findAllVisibleToUser(@Param("user") User user);

    // Used to check duplicates before create/update
    boolean existsByNameIgnoreCaseAndUser(String name, User user);

    // Used to scope get/update/delete to owner only
    Optional<Category> findByIdAndUser(UUID id, User user);
}