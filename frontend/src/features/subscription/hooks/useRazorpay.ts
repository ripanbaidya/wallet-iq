import { useEffect, useState } from 'react';

// Razorpay global type
declare global {
  interface Window {
    Razorpay: new (options: RazorpayOptions) => RazorpayInstance;
  }
}

export interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  order_id: string;
  name: string;
  description: string;
  prefill?: {
    name?: string;
    email?: string;
  };
  theme?: {
    color?: string;
  };
  handler: (response: RazorpayPaymentResponse) => void;
  modal?: {
    ondismiss?: () => void;
  };
}

export interface RazorpayPaymentResponse {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
}

export interface RazorpayInstance {
  open: () => void;
  close: () => void;
}

const RAZORPAY_SCRIPT_URL = 'https://checkout.razorpay.com/v1/checkout.js';

export function useRazorpay() {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // Already loaded
    if (window.Razorpay) {
      setIsLoaded(true);
      return;
    }

    // Already being loaded by another instance
    const existing = document.querySelector(
      `script[src="${RAZORPAY_SCRIPT_URL}"]`
    );
    if (existing) {
      existing.addEventListener('load', () => setIsLoaded(true));
      return;
    }

    // Load the script
    setIsLoading(true);
    const script = document.createElement('script');
    script.src = RAZORPAY_SCRIPT_URL;
    script.async = true;
    script.onload = () => {
      setIsLoaded(true);
      setIsLoading(false);
    };
    script.onerror = () => {
      setIsLoading(false);
      console.error('Failed to load Razorpay SDK');
    };
    document.body.appendChild(script);
  }, []);

  const openPaymentModal = (options: RazorpayOptions): void => {
    if (!window.Razorpay) {
      console.error('Razorpay SDK not loaded');
      return;
    }
    const rzp = new window.Razorpay(options);
    rzp.open();
  };

  return { isLoaded, isLoading, openPaymentModal };
}