import { apiClient } from "../lib/axios";
import type { ResponseWrapper } from "../types/api.types";
import type { CreateTransactionRequest, PageResponse, TransactionResponse, UpdateTransactionRequest } from "../types/transaction.types";

export const transactionService = {
  getAll: async (params?: {
    type?: string;
    categoryId?: string;
    dateFrom?: string;
    dateTo?: string;
    page?: number;
    size?: number;
  }): Promise<ResponseWrapper<PageResponse<TransactionResponse>>> => {
    const res = await apiClient.get('/transactions', { params });
    return res.data;
  },
  getById: async (id: string): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.get(`/transactions/${id}`);
    return res.data;
  },
  create: async (data: CreateTransactionRequest): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.post('/transactions', data);
    return res.data;
  },
  update: async (id: string, data: UpdateTransactionRequest): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.put(`/transactions/${id}`, data);
    return res.data;
  },
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/transactions/${id}`);
  }
}