package io.adzubla.blocks.sample.payment.domain.repository;

import io.adzubla.blocks.sample.payment.domain.model.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository {

    void save(Payment payment);

    /** All payment attempts for an order. Phase 1 can record more than one (see TODO(idempotency)). */
    List<Payment> findByOrderId(UUID orderId);

    List<Payment> findAll();
}
