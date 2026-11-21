package io.adzubla.blocks.sample.payment.infrastructure.persistence;

import io.adzubla.blocks.sample.payment.domain.model.Payment;
import io.adzubla.blocks.sample.payment.domain.model.PaymentStatus;
import io.adzubla.blocks.sample.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentRepository jpa;

    JpaPaymentRepository(SpringDataPaymentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Payment p) {
        jpa.save(new PaymentJpaEntity(p.id(), p.orderId(), p.amount(), p.status().name(), p.gatewayTransactionId(),
                p.failureReason(), p.createdAt()));
    }

    @Override
    public List<Payment> findByOrderId(UUID orderId) {
        return jpa.findByOrderIdOrderByCreatedAt(orderId).stream().map(JpaPaymentRepository::toDomain).toList();
    }

    @Override
    public List<Payment> findAll() {
        return jpa.findTop50ByOrderByCreatedAtDesc().stream().map(JpaPaymentRepository::toDomain).toList();
    }

    private static Payment toDomain(PaymentJpaEntity e) {
        return Payment.restore(e.getId(), e.getOrderId(), e.getAmount(), PaymentStatus.valueOf(e.getStatus()),
                e.getGatewayTransactionId(), e.getFailureReason(), e.getCreatedAt());
    }
}
