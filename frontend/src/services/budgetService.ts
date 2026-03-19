import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type {
  BudgetResponse,
  BudgetStatusResponse,
  CreateBudgetRequest,
} from '../types/budget.types';

export const budgetService = {
  create: async (data: CreateBudgetRequest): Promise<ResponseWrapper<BudgetResponse>> => {
    const res = await apiClient.post('/budgets', data);
    return res.data;
  },

  getByMonth: async (month: string): Promise<ResponseWrapper<BudgetResponse[]>> => {
    const res = await apiClient.get('/budgets', { params: { month } });
    return res.data;
  },

  getStatus: async (id: string): Promise<ResponseWrapper<BudgetStatusResponse>> => {
    const res = await apiClient.get(`/budgets/${id}/status`);
    return res.data;
  },
};