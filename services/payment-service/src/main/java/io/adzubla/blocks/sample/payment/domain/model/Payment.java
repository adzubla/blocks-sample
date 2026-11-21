package io.adzubla.blocks.sample.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root: one attempt to charge a customer for an order. Immutable once created: the
 * gateway has already decided the outcome when the payment is recorded.
 */
public class Payment {

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String gatewayTransactionId;
    private final String failureReason;
    private final Instant createdAt;

    private Payment(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status, String gatewayTransactionId,
                    String failureReason, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.status = Objects.requireNonNull(status, "status");
        this.gatewayTransactionId = gatewayTransactionId;
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Payment approved(UUID orderId, BigDecimal amount, String gatewayTransactionId) {
        Objects.requireNonNull(gatewayTransactionId, "gatewayTransactionId");
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.APPROVED, gatewayTransactionId, null,
                Instant.now());
    }

    public static Payment declined(UUID orderId, BigDecimal amount, String gatewayTransactionId, String reason) {
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.DECLINED, gatewayTransactionId, reason,
                Instant.now());
    }

    public static Payment restore(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status,
                                  String gatewayTransactionId, String failureReason, Instant createdAt) {
        return new Payment(id, orderId, amount, status, gatewayTransactionId, failureReason, createdAt);
    }

    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }

    public UUID id() {
        return id;
    }

    public UUID orderId() {
        return orderId;
    }

    public BigDecimal amount() {
        return amount;
    }

    public PaymentStatus status() {
        return status;
    }

    public String gatewayTransactionId() {
        return gatewayTransactionId;
    }

    public String failureReason() {
        return failureReason;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
