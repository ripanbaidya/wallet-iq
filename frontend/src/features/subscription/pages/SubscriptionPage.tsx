import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { subscriptionService } from "../subscriptionService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { useRazorpay } from "../hooks/useRazorpay";
import { useAuthStore } from "../../../store/authStore";
import { AppError } from "../../../api/errorParser";
import Spinner from "../../../shared/components/ui/Spinner";
import { ROUTES } from "../../../routes/routePaths";

import SubscriptionStatusBanner from "../components/SubscriptionStatusBanner";
import PricingCard from "../components/PricingCard";
import PaymentProcessingOverlay from "../components/PaymentProcessingOverlay";

export default function SubscriptionPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user } = useAuthStore();
  const { isLoaded, openPaymentModal } = useRazorpay();

  const [isProcessing, setIsProcessing] = useState(false);

  const { data: statusData, isLoading: isStatusLoading } = useAppQuery({
    queryKey: ["subscription-status"],
    queryFn: () => subscriptionService.getStatus(),
  });

  const status = statusData?.data;
  const isActive = status?.isActive ?? false;

  const { mutate: verifyPayment } = useAppMutation({
    mutationFn: (data: {
      razorpayOrderId: string;
      razorpayPaymentId: string;
      razorpaySignature: string;
    }) => subscriptionService.verifyPayment(data),
    onSuccess: () => {
      setIsProcessing(false);
      queryClient.invalidateQueries({ queryKey: ["subscription-status"] });
      toast.success("Subscription activated! Enjoy AI chat.");
      navigate(ROUTES.chat);
    },
    onError: (err: AppError) => {
      setIsProcessing(false);
      toast.error(`Payment verification failed: ${err.message}`);
    },
  });

  const { mutate: createOrder, isPending: isCreatingOrder } = useAppMutation({
    mutationFn: () => subscriptionService.createOrder(),
    onSuccess: (res) => {
      const { orderId, amount, currency, keyId } = res.data;

      openPaymentModal({
        key: keyId,
        amount,
        currency,
        order_id: orderId,
        name: "WalletIQ",
        description: "30-day AI Chat Subscription",
        prefill: { name: user?.fullName, email: user?.email },
        theme: { color: "#000000" },
        handler: (response) => {
          setIsProcessing(true);
          verifyPayment({
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          });
        },
        modal: {
          ondismiss: () => toast.error("Payment cancelled"),
        },
      });
    },
    onError: (err: AppError) => toast.error(err.message),
  });

  const handleSubscribe = () => {
    if (!isLoaded) {
      toast.error("Payment system is loading. Please try again.");
      return;
    }
    createOrder(undefined);
  };

  if (isStatusLoading) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto px-4 py-8 space-y-6">
      {/* Page heading */}
      <div>
        <h1 className="text-xl font-semibold text-gray-900">
          AI Chat Subscription
        </h1>
        <p className="text-sm text-gray-500 mt-1">
          Unlock RAG-powered financial insights with a subscription.
        </p>
      </div>

      {/* Active subscription banner — only shown when subscribed */}
      {isActive && status?.expiresAt && (
        <SubscriptionStatusBanner expiresAt={status.expiresAt} />
      )}

      {/* Pricing card with features + CTA */}
      <PricingCard
        isActive={isActive}
        isCreatingOrder={isCreatingOrder}
        isProcessing={isProcessing}
        isLoaded={isLoaded}
        onSubscribe={handleSubscribe}
      />

      {/* Fullscreen overlay while verifying payment */}
      <PaymentProcessingOverlay isVisible={isProcessing} />
    </div>
  );
}
