package io.adzubla.blocks.sample.contracts.payment;

import java.util.UUID;

/** The charge for an order was declined. Published by payment-service on {@code payment.events}. */
public record PaymentFailed(UUID orderId, String reason) {
}
