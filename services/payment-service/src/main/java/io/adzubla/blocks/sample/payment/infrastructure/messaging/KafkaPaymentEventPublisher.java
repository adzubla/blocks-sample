package io.adzubla.blocks.sample.payment.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.payment.PaymentCompleted;
import io.adzubla.blocks.sample.contracts.payment.PaymentFailed;
import io.adzubla.blocks.sample.payment.application.port.PaymentEventPublisher;
import io.adzubla.blocks.sample.payment.domain.model.Payment;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

/** Adapter: publishes payment events to Kafka. TODO(outbox). */
@Component
class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    KafkaPaymentEventPublisher(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    @Override
    public void paymentCompleted(Payment payment) {
        publish(payment.orderId(), new PaymentCompleted(payment.orderId(), payment.id()));
    }

    @Override
    public void paymentFailed(Payment payment) {
        publish(payment.orderId(), new PaymentFailed(payment.orderId(), payment.failureReason()));
    }

    private void publish(UUID orderId, Object event) {
        String key = orderId.toString();
        var record = new ProducerRecord<>(Topics.PAYMENT_EVENTS, key, json.writeValueAsString(event));
        MessageContext.addHeaders(record.headers(), event, key);
        kafka.send(record).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to publish {} for order {}", event.getClass().getSimpleName(), key, error);
            }
        });
    }
}
