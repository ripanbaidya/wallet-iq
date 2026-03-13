package com.walletiq.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentModeServiceImpl implements PaymentModeService {

    private final PaymentModeRepository paymentModeRepository;
    private final UserRepository userRepository;

    // Get all payment modes visible to the current user

    @Override
    @Transactional(readOnly = true)
    public List<PaymentModeResponse> getAllPaymentModes() {
        return paymentModeRepository.findAllVisibleToUser(currentUser())
            .stream()
            .map(PaymentModeMapper::toResponse)
            .toList();
    }

    // Create payment mode for user

    @Override
    @Transactional
    public PaymentModeResponse createPaymentMode(CreatePaymentModeRequest request) {
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

    // Update payment mode

    @Override
    @Transactional
    public PaymentModeResponse updatePaymentMode(UUID id, UpdatePaymentModeRequest request) {
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

    // Delete payment mode

    @Override
    @Transactional
    public void deletePaymentMode(UUID id) {
        User currentUser = currentUser();

        PaymentMode paymentMode = findPaymentModeByIdAndUser(id, currentUser);

        paymentModeRepository.delete(paymentMode);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMode findById(UUID id) {
        return paymentModeRepository.findById(id)
            .orElseThrow(() -> new PaymentModeException(ErrorCode.PAYMENT_MODE_NOT_FOUND));
    }

    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    private PaymentMode findPaymentModeByIdAndUser(UUID id, User user) {
        return paymentModeRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new PaymentModeException(
                ErrorCode.PAYMENT_MODE_NOT_FOUND,
                "Payment mode not found or you do not have permission to access it"
            ));
    }
}