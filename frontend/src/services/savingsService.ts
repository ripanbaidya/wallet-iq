import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type {
  GoalProgressResponse,
  CreateSavingsGoalRequest,
  ContributeRequest,
} from '../types/savings.types';

export const savingsService = {
  getAll: async (): Promise<ResponseWrapper<GoalProgressResponse[]>> => {
    const res = await apiClient.get('/goals');
    return res.data;
  },

  create: async (data: CreateSavingsGoalRequest): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.post('/goals', data);
    return res.data;
  },

  contribute: async (id: string, data: ContributeRequest): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.patch(`/goals/${id}/contribute`, data);
    return res.data;
  },

  getProgress: async (id: string): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.get(`/goals/${id}/progress`);
    return res.data;
  },
};