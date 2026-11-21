package io.adzubla.blocks.sample.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    List<PaymentJpaEntity> findByOrderIdOrderByCreatedAt(UUID orderId);

    List<PaymentJpaEntity> findTop50ByOrderByCreatedAtDesc();
}
