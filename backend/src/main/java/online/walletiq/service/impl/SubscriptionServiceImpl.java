package online.walletiq.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import online.walletiq.config.properties.RazorpayProperties;
import online.walletiq.dto.subscription.CreateOrderResponse;
import online.walletiq.dto.subscription.SubscriptionStatusResponse;
import online.walletiq.dto.subscription.VerifyPaymentRequest;
import online.walletiq.entity.Subscription;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.enums.SubscriptionStatus;
import online.walletiq.exception.SubscriptionException;
import online.walletiq.repository.SubscriptionRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.SubscriptionService;
import online.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public CreateOrderResponse createOrder() {
        User user = currentUser();
        UUID userId = user.getId();

        // Check if user already has an active subscription
        boolean alreadyActive = subscriptionRepository.findActiveByUserId(userId, Instant.now())
            .isPresent();

        if (alreadyActive) {
            throw new SubscriptionException(ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE);
        }

        try {
            // Create razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", razorpayProperties.subscriptionAmount());
            orderRequest.put("amount", razorpayProperties.subscriptionAmount());
            orderRequest.put("currency", razorpayProperties.currency());
            orderRequest.put("receipt", "receipt_" + userId.toString().substring(0, 8));

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id"); // Get the order id

            // Only order is created, Save subscription as pending
            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setRazorpayOrderId(orderId);
            subscription.setAmount(razorpayProperties.subscriptionAmount());
            subscription.setCurrency(razorpayProperties.currency());
            subscription.setStatus(SubscriptionStatus.PENDING);

            subscriptionRepository.save(subscription);

            log.info("Razorpay order created: {} for user: {}", orderId, userId);

            return new CreateOrderResponse(
                orderId,
                razorpayProperties.subscriptionAmount(),
                razorpayProperties.currency(),
                razorpayProperties.keyId()
            );

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for user: {}", userId, e);
            throw new SubscriptionException(ErrorCode.PAYMENT_ORDER_CREATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void verifyPayment(VerifyPaymentRequest request) {

        try {
            // Verify the Signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.razorpayOrderId());
            attributes.put("razorpay_payment_id", request.razorpayPaymentId());
            attributes.put("razorpay_signature", request.razorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(
                attributes, razorpayProperties.keySecret()
            );

            if (!isValid) {
                log.warn("Invalid Razorpay signature for order: {}", request.razorpayOrderId());
                throw new SubscriptionException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
            }

        } catch (RazorpayException e) {
            log.error("Signature verification failed for order: {}", request.razorpayOrderId(), e);
            throw new SubscriptionException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
        }

        // Find the pending subscription
        Subscription subscription = subscriptionRepository
            .findByRazorpayOrderId(request.razorpayOrderId())
            .orElseThrow(() -> new SubscriptionException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        Instant now = Instant.now();
        subscription.setRazorpayPaymentId(request.razorpayPaymentId());
        subscription.setRazorpaySignature(request.razorpaySignature());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartsAt(now);
        subscription.setExpiresAt(now.plus(
            razorpayProperties.subscriptionDays(), ChronoUnit.DAYS
        ));

        subscriptionRepository.save(subscription);

        log.info("Subscription activated for order: {} payment: {}", request.razorpayOrderId(),
            request.razorpayPaymentId());
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getStatus() {
        UUID userId = currentUserId();

        return subscriptionRepository.findActiveByUserId(userId, Instant.now())
            .map(s -> new SubscriptionStatusResponse(true, s.getExpiresAt(), s.getStatus()))
            .orElse(new SubscriptionStatusResponse(false, null, SubscriptionStatus.EXPIRED));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(UUID userId) {
        return subscriptionRepository.findActiveByUserId(userId, Instant.now())
            .isPresent();
    }

    @Override
    @Transactional
    public void markExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findAllExpired(Instant.now());
        expiredSubscriptions.forEach(subscription -> {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
        });
        subscriptionRepository.saveAll(expiredSubscriptions);
        log.info("Marked {} subscriptions as expired", expiredSubscriptions.size());
    }

    // Helper

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    private UUID currentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}
