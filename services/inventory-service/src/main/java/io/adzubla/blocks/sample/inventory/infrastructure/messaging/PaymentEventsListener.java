package io.adzubla.blocks.sample.inventory.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import io.adzubla.blocks.sample.contracts.Topics;
import io.adzubla.blocks.sample.contracts.payment.PaymentFailed;
import io.adzubla.blocks.sample.inventory.application.ReleaseStockUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adapter: releases the stock when payment fails (choreographed compensation).
 * <p>
 * Inventory has to know about payment's events just to undo its own work. TODO(saga): the
 * orchestrator will send a ReleaseStock command instead.
 */
@Component
class PaymentEventsListener {

    private final ReleaseStockUseCase releaseStock;
    private final JsonMapper json;

    PaymentEventsListener(ReleaseStockUseCase releaseStock, JsonMapper json) {
        this.releaseStock = releaseStock;
        this.json = json;
    }

    // TODO(idempotency): @Idempotent(header = MessageHeaders.EVENT_ID).
    @KafkaListener(topics = Topics.PAYMENT_EVENTS)
    void on(ConsumerRecord<String, String> record) {
        if (!"PaymentFailed".equals(MessageContext.header(record, MessageHeaders.EVENT_TYPE))) {
            return;
        }
        var event = json.readValue(record.value(), PaymentFailed.class);
        MessageContext.handle(record, () -> releaseStock.release(event.orderId()));
    }
}
