package online.walletiq.repository;

import online.walletiq.entity.PaymentMode;
import online.walletiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode, UUID> {

    // Fetch system defaults (user IS NULL) + user's own payment modes
    @Query("select p from PaymentMode p where p.user is null or p.user = :user order by p.name asc")
    List<PaymentMode> findAllVisibleToUser(@Param("user") User user);

    // Used to check duplicates before create/update
    boolean existsByNameIgnoreCaseAndUser(String name, User user);

    // Used to scope get/update/delete to owner only
    Optional<PaymentMode> findByIdAndUser(UUID id, User user);
}
