package io.adzubla.blocks.sample.payment.interfaces.rest;

import io.adzubla.blocks.sample.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, BigDecimal amount, String status, String gatewayTransactionId,
                              String failureReason, Instant createdAt) {

    static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.id(), p.orderId(), p.amount(), p.status().name(), p.gatewayTransactionId(),
                p.failureReason(), p.createdAt());
    }
}
