export interface CreateOrderResponse {
  orderId: string;
  amount: number;
  currency: string;
  keyId: string;
}

export interface VerifyPaymentRequest {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

export interface SubscriptionStatusResponse {
  isActive: boolean;
  expiresAt: string | null;
  status: 'ACTIVE' | 'EXPIRED' | 'PENDING' | 'FAILED';
}