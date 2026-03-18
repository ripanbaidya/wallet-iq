package com.walletiq.service.impl;

import com.walletiq.constant.CacheNames;
import com.walletiq.dto.paymentmode.CreatePaymentModeRequest;
import com.walletiq.dto.paymentmode.PaymentModeResponse;
import com.walletiq.dto.paymentmode.UpdatePaymentModeRequest;
import com.walletiq.entity.PaymentMode;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.PaymentModeException;
import com.walletiq.mapper.PaymentModeMapper;
import com.walletiq.repository.PaymentModeRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.PaymentModeService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link PaymentModeService}.
 * <p>Handles payment mode management for the current user, including creation,
 * update, deletion, and retrieval of both user-defined and system default payment modes.
 * <p>Also manages cache consistency for payment mode operations.
 */
@Service
@RequiredArgsConstructor
public class PaymentModeServiceImpl implements PaymentModeService {

    private final UserRepository userRepository;
    private final PaymentModeRepository paymentModeRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.PAYMENT_MODES, keyGenerator = "userKeyGenerator")
    public List<PaymentModeResponse> getAll() {
        return paymentModeRepository.findAllVisibleToUser(currentUser())
            .stream()
            .map(PaymentModeMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.PAYMENT_MODES, keyGenerator = "userKeyGenerator")
    public PaymentModeResponse create(CreatePaymentModeRequest request) {
        User currentUser = currentUser();

        if (paymentModeRepository.existsByNameIgnoreCaseAndUser(request.name(), currentUser)) {
            throw new PaymentModeException(
                ErrorCode.PAYMENT_MODE_ALREADY_EXISTS,
                "A payment mode named '%s' already exists".formatted(request.name())
            );
        }

        PaymentMode paymentMode = new PaymentMode();
        paymentMode.setName(request.name().trim());
        paymentMode.setUser(currentUser);

        return PaymentModeMapper.toResponse(paymentModeRepository.save(paymentMode));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.PAYMENT_MODES, keyGenerator = "userKeyGenerator")
    public PaymentModeResponse update(UUID id, UpdatePaymentModeRequest request) {
        User currentUser = currentUser();

        PaymentMode paymentMode = findPaymentModeByIdAndUser(id, currentUser);

        String newName = request.name().trim();

        if (!paymentMode.getName().equalsIgnoreCase(newName)
            && paymentModeRepository.existsByNameIgnoreCaseAndUser(newName, currentUser)) {
            throw new PaymentModeException(
                ErrorCode.PAYMENT_MODE_ALREADY_EXISTS,
                "A payment mode named '%s' already exists".formatted(newName)
            );
        }

        paymentMode.setName(newName);
        return PaymentModeMapper.toResponse(paymentModeRepository.save(paymentMode));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.PAYMENT_MODES, keyGenerator = "userKeyGenerator")
    public void delete(UUID id) {
        User currentUser = currentUser();

        PaymentMode paymentMode = findPaymentModeByIdAndUser(id, currentUser);

        paymentModeRepository.delete(paymentMode);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.PAYMENT_MODES, keyGenerator = "userKeyGenerator")
    public PaymentMode findById(UUID id) {
        return paymentModeRepository.findById(id)
            .orElseThrow(() -> new PaymentModeException(ErrorCode.PAYMENT_MODE_NOT_FOUND));
    }

    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }


    /**
     * Finds a payment mode owned by the given user.
     * <p>Ensures that only user-owned payment modes are accessible.
     *
     * @throws PaymentModeException if payment mode is not found or not accessible
     */
    private PaymentMode findPaymentModeByIdAndUser(UUID id, User user) {
        return paymentModeRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new PaymentModeException(
                ErrorCode.PAYMENT_MODE_NOT_FOUND,
                "Payment mode not found or you do not have permission to access it"
            ));
    }
}