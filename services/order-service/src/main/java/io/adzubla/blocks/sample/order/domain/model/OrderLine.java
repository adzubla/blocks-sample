package io.adzubla.blocks.sample.order.domain.model;

import java.util.Objects;

/** One product and quantity within an {@link Order}, priced when the order was placed. */
public record OrderLine(String sku, int quantity, Money unitPrice) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
    }

    public Money subtotal() {
        return unitPrice.times(quantity);
    }
}
