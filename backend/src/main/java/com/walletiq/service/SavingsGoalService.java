package com.walletiq.service;

import com.walletiq.dto.savingsgoal.ContributeRequest;
import com.walletiq.dto.savingsgoal.GoalProgressResponse;
import com.walletiq.dto.savingsgoal.SavingsGoalRequest;

import java.util.List;
import java.util.UUID;

public interface SavingsGoalService {

    GoalProgressResponse create(SavingsGoalRequest request);

    List<GoalProgressResponse> getAll();

    GoalProgressResponse contribute(UUID goalId, ContributeRequest request);

    GoalProgressResponse getProgress(UUID goalId);

    void markExpiredGoalsAsFailed();
}
