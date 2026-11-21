package io.adzubla.blocks.sample.inventory.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.order.OrderPlaced;
import io.adzubla.blocks.sample.inventory.application.ReserveStockCommand;
import io.adzubla.blocks.sample.inventory.application.ReserveStockUseCase;
import io.adzubla.blocks.sample.inventory.domain.model.ReservedLine;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/** Adapter: reserves stock when an order is placed. */
@Component
class OrderEventsListener {

    private final ReserveStockUseCase reserveStock;
    private final JsonMapper json;

    OrderEventsListener(ReserveStockUseCase reserveStock, JsonMapper json) {
        this.reserveStock = reserveStock;
        this.json = json;
    }

    // TODO(idempotency): @Idempotent(header = MessageHeaders.EVENT_ID). A redelivered
    // OrderPlaced reserves the stock twice.
    @KafkaListener(topics = Topics.ORDER_EVENTS)
    void on(ConsumerRecord<String, String> record) {
        if (!"OrderPlaced".equals(MessageContext.header(record, MessageHeaders.EVENT_TYPE))) {
            return;
        }
        var event = json.readValue(record.value(), OrderPlaced.class);
        MessageContext.handle(record, () -> reserveStock.reserve(new ReserveStockCommand(
                event.orderId(), event.customerEmail(), event.total(),
                event.lines().stream().map(l -> new ReservedLine(l.sku(), l.quantity())).toList())));
    }
}
