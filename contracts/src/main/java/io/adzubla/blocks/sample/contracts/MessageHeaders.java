package io.adzubla.blocks.sample.contracts;

/**
 * Header names that every message (Kafka record or RabbitMQ message) carries.
 * <p>
 * Phase 1 sets them but barely uses them. They are there so later phases can build on them:
 * <ul>
 *   <li>{@link #EVENT_ID}: unique per message. TODO(idempotency): the deduplication key for
 *       {@code @Idempotent} listeners.</li>
 *   <li>{@link #EVENT_TYPE}: the simple class name of the payload, used to pick the
 *       deserialization target.</li>
 *   <li>{@link #CORRELATION_ID}: the business flow the message belongs to (the orderId).
 *       TODO(saga): the saga instance id.</li>
 *   <li>{@link #CAUSATION_ID}: the {@link #EVENT_ID} of the message that caused this one, or
 *       empty for messages caused by an HTTP request.</li>
 * </ul>
 */
public final class MessageHeaders {

    public static final String EVENT_ID = "eventId";
    public static final String EVENT_TYPE = "eventType";
    public static final String CORRELATION_ID = "correlationId";
    public static final String CAUSATION_ID = "causationId";

    private MessageHeaders() {
    }
}
