package com.walletiq.service;

import com.walletiq.dto.savingsgoal.ContributeRequest;
import com.walletiq.dto.savingsgoal.GoalProgressResponse;
import com.walletiq.dto.savingsgoal.SavingsGoalRequest;
import com.walletiq.exception.SavingsGoalException;

import java.util.List;
import java.util.UUID;

public interface SavingsGoalService {

    /**
     * Creates a new savings goal for the current user.
     * <p>Initializes the goal with zero saved amount and status as IN_PROGRESS.
     *
     * @param request savings goal creation request
     * @return created goal progress response
     */
    GoalProgressResponse create(SavingsGoalRequest request);

    /**
     * Retrieves all savings goals for the current user.
     *
     * @return list of goal progress responses
     */
    List<GoalProgressResponse> getAll();

    /**
     * Contributes an amount towards a savings goal.
     * <p>Updates the saved amount and marks the goal as ACHIEVED if the
     * target amount is reached or exceeded.
     *
     * @param goalId  goal identifier
     * @param request contribution request
     * @return updated goal progress response
     * @throws SavingsGoalException if the goal is not found, access is denied,
     *                              or the goal is already ACHIEVED or FAILED
     */
    GoalProgressResponse contribute(UUID goalId, ContributeRequest request);

    /**
     * Retrieves the progress of a specific savings goal.
     *
     * @param goalId goal identifier
     * @return goal progress response
     * @throws SavingsGoalException if the goal is not found or access is denied
     */
    GoalProgressResponse getProgress(UUID goalId);

    /**
     * Marks all expired goals as FAILED.
     * <p>A goal is considered expired if - Status is IN_PROGRESS,
     * Deadline is before the current date
     * <p>Typically invoked by a scheduled job.
     */
    void markExpiredGoalsAsFailed();
}
