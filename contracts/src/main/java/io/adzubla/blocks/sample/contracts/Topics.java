package io.adzubla.blocks.sample.contracts;

/**
 * Kafka topics carrying domain events. Each topic is owned by exactly one bounded context:
 * only the owning service publishes to it, any service may consume from it.
 * <p>
 * Messages are keyed by {@code orderId}, so all events about one order land in the same
 * partition and are consumed in order.
 */
public final class Topics {

    /** Owned by order-service. */
    public static final String ORDER_EVENTS = "order.events";

    /** Owned by inventory-service. */
    public static final String INVENTORY_EVENTS = "inventory.events";

    /** Owned by payment-service. */
    public static final String PAYMENT_EVENTS = "payment.events";

    private Topics() {
    }
}
