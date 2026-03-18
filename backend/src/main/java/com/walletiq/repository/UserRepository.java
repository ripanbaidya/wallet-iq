package com.walletiq.repository;

import com.walletiq.entity.User;
import com.walletiq.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u from User u where u.active = true and u.role <> com.walletiq.enums.Role.ADMIN")
    List<User> findAllActiveUsers();

    long countByRoleAndActive(Role role, boolean active);
}
