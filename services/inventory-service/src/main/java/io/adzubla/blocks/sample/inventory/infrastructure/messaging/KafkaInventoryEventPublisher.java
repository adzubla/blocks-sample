package io.adzubla.blocks.sample.inventory.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.inventory.StockReservationFailed;
import io.adzubla.blocks.sample.contracts.inventory.StockReserved;
import io.adzubla.blocks.sample.inventory.application.port.InventoryEventPublisher;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.UUID;

/** Adapter: publishes inventory events to Kafka. TODO(outbox). */
@Component
class KafkaInventoryEventPublisher implements InventoryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaInventoryEventPublisher.class);

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    KafkaInventoryEventPublisher(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    @Override
    public void stockReserved(UUID orderId, String customerEmail, BigDecimal amount) {
        publish(orderId, new StockReserved(orderId, customerEmail, amount));
    }

    @Override
    public void stockReservationFailed(UUID orderId, String reason) {
        publish(orderId, new StockReservationFailed(orderId, reason));
    }

    private void publish(UUID orderId, Object event) {
        String key = orderId.toString();
        var record = new ProducerRecord<>(Topics.INVENTORY_EVENTS, key, json.writeValueAsString(event));
        MessageContext.addHeaders(record.headers(), event, key);
        kafka.send(record).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to publish {} for order {}", event.getClass().getSimpleName(), key, error);
            }
        });
    }
}
