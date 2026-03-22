import { useAppQuery } from '../../../shared/hooks/useAppQuery';
import { subscriptionService } from '../subscriptionService';

export function useSubscriptionGuard() {
  const { data, isLoading } = useAppQuery({
    queryKey: ['subscription-status'],
    queryFn: () => subscriptionService.getStatus(),
    staleTime: 1000 * 60 * 5,
  });

  const isActive = data?.data?.isActive ?? false;

  return { isLoading, isActive };
}