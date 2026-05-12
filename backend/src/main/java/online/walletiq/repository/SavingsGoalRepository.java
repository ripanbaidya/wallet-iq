package online.walletiq.repository;

import online.walletiq.entity.SavingsGoal;
import online.walletiq.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {

    List<SavingsGoal> findByUser_Id(UUID userId);

    Optional<SavingsGoal> findByIdAndUser_Id(UUID id, UUID userId);

    // Used by scheduler to mark overdue goals as FAILED
    List<SavingsGoal> findByStatusAndDeadlineBefore(
        GoalStatus status,
        LocalDate date);
}