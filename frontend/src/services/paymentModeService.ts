import { apiClient } from "../lib/axios";
import type { ResponseWrapper } from "../types/api.types";
import type { CreatePaymentModeRequest, PaymentModeResponse, UpdatePaymentModeRequest } from "../types/paymentMode.types";

export const paymentModeService = {
  create: async (data: CreatePaymentModeRequest): Promise<ResponseWrapper<PaymentModeResponse>> => {
    const res = await apiClient.post('/payment-modes', data);
    return res.data;
  },
  getAll: async (): Promise<ResponseWrapper<PaymentModeResponse[]>> => {
    const res = await apiClient.get('/payment-modes');
    return res.data;
  },
  update: async (id: string, data: UpdatePaymentModeRequest): Promise<ResponseWrapper<PaymentModeResponse>> => {
    const res = await apiClient.put(`/payment-modes/${id}`, data);
    return res.data;
  },
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/payment-modes/${id}`);
  }
}