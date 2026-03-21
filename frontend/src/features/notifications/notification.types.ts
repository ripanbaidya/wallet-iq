export type NotificationType =
  | 'BUDGET_ALERT'
  | 'RECURRING_TRANSACTION'
  | 'SAVINGS_GOAL'
  | 'LOGIN_ALERT'
  | 'SYSTEM';

export interface NotificationResponse {
  id: string;
  type: NotificationType;
  message: string;
  createdAt: string;
}