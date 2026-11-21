package io.adzubla.blocks.sample.order.application;

import io.adzubla.blocks.sample.order.domain.model.OrderId;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(OrderId id) {
        super("Order not found: " + id);
    }
}
