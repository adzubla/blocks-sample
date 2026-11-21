package io.adzubla.blocks.sample.order.domain.model;

/**
 * Lifecycle of an order:
 * <pre>
 *   PENDING ──▶ CONFIRMED
 *      │
 *      └─────▶ CANCELLED
 * </pre>
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
