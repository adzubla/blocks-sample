package io.adzubla.blocks.sample.contracts.payment;

import java.util.UUID;

/** The customer was charged for an order. Published by payment-service on {@code payment.events}. */
public record PaymentCompleted(UUID orderId, UUID paymentId) {
}
