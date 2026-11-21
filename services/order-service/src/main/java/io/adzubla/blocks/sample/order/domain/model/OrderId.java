package io.adzubla.blocks.sample.order.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Identity of the {@link Order} aggregate. */
public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "value");
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
