package online.walletiq.service.impl;

import online.walletiq.dto.savingsgoal.ContributeRequest;
import online.walletiq.dto.savingsgoal.GoalProgressResponse;
import online.walletiq.dto.savingsgoal.SavingsGoalRequest;
import online.walletiq.entity.SavingsGoal;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.enums.GoalStatus;
import online.walletiq.enums.NotificationType;
import online.walletiq.exception.SavingsGoalException;
import online.walletiq.mapper.SavingsGoalMapper;
import online.walletiq.repository.SavingsGoalRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.NotificationService;
import online.walletiq.service.SavingsGoalService;
import online.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link SavingsGoalService}.
 * <p>Handles savings goal lifecycle including creation, contribution,
 * progress tracking, and automatic expiration handling.
 * <p>Ensures ownership validation and enforces goal state transitions
 * such as IN_PROGRESS → ACHIEVED or FAILED.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final UserRepository userRepository;
    private final SavingsGoalRepository goalRepository;

    private final NotificationService notificationService;

    @Override
    @Transactional
    public GoalProgressResponse create(SavingsGoalRequest request) {
        User user = currentUser();

        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount());
        goal.setSavedAmount(BigDecimal.ZERO);
        goal.setDeadline(request.deadline());
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setNote(request.note());

        goalRepository.saveAndFlush(goal);

        return SavingsGoalMapper.toProgressResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalProgressResponse> getAll() {
        return goalRepository.findByUser_Id(currentUserId())
            .stream()
            .map(SavingsGoalMapper::toProgressResponse)
            .toList();
    }

    @Override
    public GoalProgressResponse contribute(UUID goalId, ContributeRequest request) {
        UUID userId = currentUserId();

        SavingsGoal goal = findOwnedOrThrow(goalId, userId);

        if (goal.getStatus() == GoalStatus.ACHIEVED) {
            throw new SavingsGoalException(ErrorCode.GOAL_ALREADY_ACHIEVED);
        }

        if (goal.getStatus() == GoalStatus.FAILED) {
            throw new SavingsGoalException(ErrorCode.GOAL_ALREADY_FAILED);
        }

        goal.setSavedAmount(goal.getSavedAmount().add(request.amount()));

        // Check if user has reached the goal
        if (goal.getSavedAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.ACHIEVED);

            notificationService.send(
                NotificationType.SAVINGS_GOAL,
                "You've achieved your goal: '" + goal.getTitle() + "'. 🎉"
            );

            log.info("Goal {} achieved by user {}", goalId, userId);
        }

        goalRepository.save(goal);

        return SavingsGoalMapper.toProgressResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalProgressResponse getProgress(UUID goalId) {
        return SavingsGoalMapper
            .toProgressResponse(findOwnedOrThrow(goalId, currentUserId()));
    }

    /**
     * Called by scheduler
     */
    @Transactional
    public void markExpiredGoalsAsFailed() {
        List<SavingsGoal> expired = goalRepository
            .findByStatusAndDeadlineBefore(GoalStatus.IN_PROGRESS, LocalDate.now());

        expired.forEach(goal -> {
            goal.setStatus(GoalStatus.FAILED);

            notificationService.send(
                NotificationType.SAVINGS_GOAL,
                "Your goal '" + goal.getTitle() + "' has failed. The deadline has passed."
            );

            log.info("Goal {} marked as FAILED — deadline passed", goal.getId());
        });

        goalRepository.saveAll(expired);
    }

    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    private UUID currentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private SavingsGoal findOwnedOrThrow(UUID id, UUID userId) {
        return goalRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> {
                boolean exists = goalRepository.existsById(id);
                return exists
                    ? new SavingsGoalException(ErrorCode.GOAL_ACCESS_DENIED)
                    : new SavingsGoalException(ErrorCode.GOAL_NOT_FOUND);
            });
    }
}