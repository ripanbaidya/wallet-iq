import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type {
  CreateOrderResponse,
  VerifyPaymentRequest,
  SubscriptionStatusResponse,
} from './subscription.types';

export const subscriptionService = {
  createOrder: async (): Promise<ResponseWrapper<CreateOrderResponse>> => {
    const res = await apiClient.post('/subscriptions/order');
    return res.data;
  },

  verifyPayment: async (
    data: VerifyPaymentRequest
  ): Promise<ResponseWrapper<void>> => {
    const res = await apiClient.post('/subscriptions/verify', data);
    return res.data;
  },

  getStatus: async (): Promise<ResponseWrapper<SubscriptionStatusResponse>> => {
    const res = await apiClient.get('/subscriptions/status');
    return res.data;
  },
};