package io.adzubla.blocks.sample.order.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.inventory.StockReservationFailed;
import io.adzubla.blocks.sample.contracts.payment.PaymentCompleted;
import io.adzubla.blocks.sample.contracts.payment.PaymentFailed;
import io.adzubla.blocks.sample.order.application.HandleOrderOutcomeUseCase;
import io.adzubla.blocks.sample.order.domain.model.OrderId;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adapter: listens to the events of inventory and payment that decide an order's outcome, and
 * calls the matching use case. Other event types on these topics are ignored.
 */
@Component
class OrderOutcomeListener {

    private static final Logger log = LoggerFactory.getLogger(OrderOutcomeListener.class);

    private final HandleOrderOutcomeUseCase useCase;
    private final JsonMapper json;

    OrderOutcomeListener(HandleOrderOutcomeUseCase useCase, JsonMapper json) {
        this.useCase = useCase;
        this.json = json;
    }

    // TODO(idempotency): annotate with @Idempotent(header = MessageHeaders.EVENT_ID) so a
    // redelivered event is skipped. Today a duplicate reaches the aggregate and fails there.
    @KafkaListener(topics = {Topics.INVENTORY_EVENTS, Topics.PAYMENT_EVENTS})
    void on(ConsumerRecord<String, String> record) {
        String type = MessageContext.header(record, MessageHeaders.EVENT_TYPE);
        MessageContext.handle(record, () -> {
            switch (type) {
                case "StockReservationFailed" -> {
                    var event = json.readValue(record.value(), StockReservationFailed.class);
                    log.info("Order {}: stock reservation failed ({})", event.orderId(), event.reason());
                    useCase.stockReservationFailed(new OrderId(event.orderId()), event.reason());
                }
                case "PaymentCompleted" -> {
                    var event = json.readValue(record.value(), PaymentCompleted.class);
                    log.info("Order {}: payment completed", event.orderId());
                    useCase.paymentCompleted(new OrderId(event.orderId()));
                }
                case "PaymentFailed" -> {
                    var event = json.readValue(record.value(), PaymentFailed.class);
                    log.info("Order {}: payment failed ({})", event.orderId(), event.reason());
                    useCase.paymentFailed(new OrderId(event.orderId()), event.reason());
                }
                default -> log.debug("Ignoring {} from {}", type, record.topic());
            }
        });
    }
}
