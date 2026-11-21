package io.adzubla.blocks.sample.order.domain.exception;

import io.adzubla.blocks.sample.order.domain.model.OrderId;
import io.adzubla.blocks.sample.order.domain.model.OrderStatus;

/** An order was asked to make a transition its current status doesn't allow. */
public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(OrderId id, OrderStatus current, String action) {
        super("Cannot " + action + " order " + id + " in status " + current);
    }
}
