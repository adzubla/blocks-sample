package io.adzubla.blocks.sample.order.interfaces.rest;

import io.adzubla.blocks.sample.order.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(UUID id, String customerEmail, String status, String cancellationReason,
                            BigDecimal total, Instant createdAt, List<Line> lines) {

    public record Line(String sku, int quantity, BigDecimal unitPrice) {
    }

    static OrderResponse from(Order order) {
        return new OrderResponse(order.id().value(), order.customerEmail(), order.status().name(),
                order.cancellationReason(), order.total().amount(), order.createdAt(),
                order.lines().stream().map(l -> new Line(l.sku(), l.quantity(), l.unitPrice().amount())).toList());
    }
}
