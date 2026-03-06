package com.walletiq.backend.repository;

import com.walletiq.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds the single active (non-revoked, non-expired) token for a user
     */
    @Query("""
        select r FROM RefreshToken r
        where r.user.id = :userId
          and r.revoked = false
          and r.expiresAt > :now
        """)
    Optional<RefreshToken> findActiveByUserId(@Param("userId") UUID userId,
                                              @Param("now") Instant now);
}
