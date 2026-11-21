package io.adzubla.blocks.sample.order.application.port;

import io.adzubla.blocks.sample.order.domain.model.Order;

/**
 * Outbound port: tells other bounded contexts what happened to orders.
 * <p>
 * TODO(outbox): today the adapter sends straight to Kafka. An outbox adapter would write the
 * event to a table in the same transaction as the order, and a relay would publish it. The use
 * cases won't change, which is why publishing goes through this port.
 */
public interface OrderEventPublisher {

    void orderPlaced(Order order);
}
