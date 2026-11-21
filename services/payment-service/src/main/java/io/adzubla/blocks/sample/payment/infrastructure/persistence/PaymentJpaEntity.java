package io.adzubla.blocks.sample.payment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PaymentJpaEntity() {
    }

    PaymentJpaEntity(UUID id, UUID orderId, BigDecimal amount, String status, String gatewayTransactionId,
                     String failureReason, Instant createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.gatewayTransactionId = gatewayTransactionId;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    UUID getId() {
        return id;
    }

    UUID getOrderId() {
        return orderId;
    }

    BigDecimal getAmount() {
        return amount;
    }

    String getStatus() {
        return status;
    }

    String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    String getFailureReason() {
        return failureReason;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
