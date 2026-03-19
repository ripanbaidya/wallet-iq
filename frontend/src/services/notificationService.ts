import { apiClient } from "../lib/axios";
import type { NotificationResponse } from "../types/notification.types";
import type { ResponseWrapper } from "../types/api.types";

export const notificationService = {
  getAll: async (): Promise<ResponseWrapper<NotificationResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<NotificationResponse[]>>('/notifications');
    return res.data;
  },
  deleteById: async (id: string): Promise<ResponseWrapper<void>> => {
    const res = await apiClient.delete(`/notifications/${id}`);
    return res.data;
  },
  deleteAll: async (): Promise<void> => {
    await apiClient.delete('/notifications');
  },
}