package io.adzubla.blocks.sample.payment.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.inventory.StockReserved;
import io.adzubla.blocks.sample.payment.application.ChargePaymentUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adapter: charges the customer once their stock is reserved.
 * <p>
 * TODO(saga): with orchestration, payment reacts to a ChargePayment command instead, and no
 * longer needs to know about inventory's events.
 */
@Component
class InventoryEventsListener {

    private final ChargePaymentUseCase chargePayment;
    private final JsonMapper json;

    InventoryEventsListener(ChargePaymentUseCase chargePayment, JsonMapper json) {
        this.chargePayment = chargePayment;
        this.json = json;
    }

    // TODO(idempotency): @Idempotent(header = MessageHeaders.EVENT_ID). A redelivered
    // StockReserved charges the customer twice.
    @KafkaListener(topics = Topics.INVENTORY_EVENTS)
    void on(ConsumerRecord<String, String> record) {
        if (!"StockReserved".equals(MessageContext.header(record, MessageHeaders.EVENT_TYPE))) {
            return;
        }
        var event = json.readValue(record.value(), StockReserved.class);
        MessageContext.handle(record, () -> chargePayment.charge(event.orderId(), event.amount()));
    }
}
