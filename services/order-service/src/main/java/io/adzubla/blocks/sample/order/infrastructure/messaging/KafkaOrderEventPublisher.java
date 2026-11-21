package io.adzubla.blocks.sample.order.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.order.OrderPlaced;
import io.adzubla.blocks.sample.order.application.port.OrderEventPublisher;
import io.adzubla.blocks.sample.order.domain.model.Order;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adapter: publishes order events to Kafka, translating the domain {@link Order} into the
 * {@code contracts} event. Keyed by orderId so one order's events stay in order.
 * <p>
 * TODO(outbox): replace with an adapter that writes to an outbox table.
 */
@Component
class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    KafkaOrderEventPublisher(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    @Override
    public void orderPlaced(Order order) {
        var event = new OrderPlaced(
                order.id().value(),
                order.customerEmail(),
                order.lines().stream().map(l -> new OrderPlaced.Line(l.sku(), l.quantity())).toList(),
                order.total().amount());
        String key = order.id().toString();
        var record = new ProducerRecord<>(Topics.ORDER_EVENTS, key, json.writeValueAsString(event));
        MessageContext.addHeaders(record.headers(), event, key);
        // Fire and forget: a failed send is only logged.
        kafka.send(record).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to publish OrderPlaced for order {}", key, error);
            }
        });
        log.info("Published OrderPlaced for order {}", key);
    }
}
