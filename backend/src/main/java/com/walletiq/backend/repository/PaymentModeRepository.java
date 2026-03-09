package com.walletiq.backend.repository;

import com.walletiq.backend.entity.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode, UUID> {
}
