package io.adzubla.blocks.sample.contracts.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** A customer placed an order. Published by order-service on {@code order.events}. */
public record OrderPlaced(UUID orderId, String customerEmail, List<Line> lines, BigDecimal total) {

    public record Line(String sku, int quantity) {
    }
}
