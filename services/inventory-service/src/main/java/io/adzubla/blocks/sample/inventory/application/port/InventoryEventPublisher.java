package io.adzubla.blocks.sample.inventory.application.port;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Outbound port: tells other bounded contexts the outcome of stock reservations.
 * <p>
 * TODO(outbox): today the adapter sends straight to Kafka.
 */
public interface InventoryEventPublisher {

    void stockReserved(UUID orderId, String customerEmail, BigDecimal amount);

    void stockReservationFailed(UUID orderId, String reason);
}
