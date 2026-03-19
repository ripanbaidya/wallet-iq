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
  },

  /* Export transaction as csv format */
  exportCsv: async (): Promise<void> => {
    const res = await apiClient.get('/transactions/export/csv',
      { responseType: 'blob' }
    );
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = url;
    const today = new Date().toISOString().split('T')[0];
    link.setAttribute('download', `walletiq-transactions-${today}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }
}