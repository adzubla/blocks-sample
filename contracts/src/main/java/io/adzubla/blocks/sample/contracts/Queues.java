package io.adzubla.blocks.sample.contracts;

/**
 * RabbitMQ queues carrying commands. A command has exactly one consumer: the service that
 * owns the queue.
 * <p>
 * TODO(saga): the saga orchestrator's commands (ReserveStock, ChargePayment, ReleaseStock)
 * will get their own queues here.
 */
public final class Queues {

    /** Consumed by notification-service. */
    public static final String NOTIFICATION_EMAIL = "notification.email";

    private Queues() {
    }
}
