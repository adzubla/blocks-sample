package io.adzubla.blocks.sample.payment.application.port;

import io.adzubla.blocks.sample.payment.domain.model.Payment;

/** Outbound port: tells other bounded contexts the outcome of a payment. TODO(outbox). */
public interface PaymentEventPublisher {

    void paymentCompleted(Payment payment);

    void paymentFailed(Payment payment);
}
