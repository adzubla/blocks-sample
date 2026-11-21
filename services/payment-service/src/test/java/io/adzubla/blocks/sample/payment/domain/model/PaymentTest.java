package io.adzubla.blocks.sample.payment.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    void approvedPayment() {
        Payment p = Payment.approved(UUID.randomUUID(), new BigDecimal("10.00"), "tx-1");

        assertThat(p.isApproved()).isTrue();
        assertThat(p.failureReason()).isNull();
    }

    @Test
    void declinedPaymentKeepsReason() {
        Payment p = Payment.declined(UUID.randomUUID(), new BigDecimal("2000.00"), "tx-2", "limit exceeded");

        assertThat(p.isApproved()).isFalse();
        assertThat(p.status()).isEqualTo(PaymentStatus.DECLINED);
        assertThat(p.failureReason()).isEqualTo("limit exceeded");
    }
}
