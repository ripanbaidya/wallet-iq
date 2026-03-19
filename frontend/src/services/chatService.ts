import { apiClient } from '../lib/axios';
import type { ResponseWrapper } from '../types/api.types';
import type {
  ChatSessionResponse,
  ChatMessageResponse,
  CreateSessionRequest,
  ChatQueryRequest,
  ChatQueryResponse,
} from '../types/chat.types';

export const chatService = {
  getSessions: async (): Promise<ResponseWrapper<ChatSessionResponse[]>> => {
    const res = await apiClient.get('/chat/sessions');
    return res.data;
  },

  createSession: async (data: CreateSessionRequest): Promise<ResponseWrapper<ChatSessionResponse>> => {
    const res = await apiClient.post('/chat/sessions', data);
    return res.data;
  },

  deleteSession: async (id: string): Promise<void> => {
    await apiClient.delete(`/chat/sessions/${id}`);
  },

  getMessages: async (sessionId: string): Promise<ResponseWrapper<ChatMessageResponse[]>> => {
    const res = await apiClient.get(`/chat/sessions/${sessionId}/messages`);
    return res.data;
  },

  query: async (sessionId: string, data: ChatQueryRequest): Promise<ResponseWrapper<ChatQueryResponse>> => {
    const res = await apiClient.post(`/chat/sessions/${sessionId}/query`, data);
    return res.data;
  },
};