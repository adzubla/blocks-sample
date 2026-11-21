package io.adzubla.blocks.sample.inventory.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Message metadata helpers for this service's messaging adapters.
 * <p>
 * While a listener handles a message, its {@code eventId} is kept in a thread-local, so any
 * message published as a result gets it as {@code causationId}. That way the application layer
 * never has to pass messaging metadata around.
 */
final class MessageContext {

    private static final ThreadLocal<String> CAUSATION_ID = new ThreadLocal<>();

    private MessageContext() {
    }

    /** Runs {@code handler} with the incoming record's eventId as the causation of anything published. */
    static void handle(ConsumerRecord<String, String> record, Runnable handler) {
        CAUSATION_ID.set(header(record, MessageHeaders.EVENT_ID));
        try {
            handler.run();
        } finally {
            CAUSATION_ID.remove();
        }
    }

    /** Adds the standard headers to an outgoing Kafka record. */
    static void addHeaders(Headers headers, Object payload, Object correlationId) {
        put(headers, MessageHeaders.EVENT_ID, UUID.randomUUID().toString());
        put(headers, MessageHeaders.EVENT_TYPE, payload.getClass().getSimpleName());
        put(headers, MessageHeaders.CORRELATION_ID, String.valueOf(correlationId));
        put(headers, MessageHeaders.CAUSATION_ID, causationId());
    }

    static String causationId() {
        String id = CAUSATION_ID.get();
        return id == null ? "" : id;
    }

    static String header(ConsumerRecord<?, ?> record, String name) {
        Header header = record.headers().lastHeader(name);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

    private static void put(Headers headers, String name, String value) {
        headers.add(name, value.getBytes(StandardCharsets.UTF_8));
    }
}
