package io.adzubla.blocks.sample.inventory.application;

import io.adzubla.blocks.sample.inventory.domain.model.ReservedLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Input of {@link ReserveStockUseCase}. {@code customerEmail} and {@code amount} aren't needed
 * to reserve stock. They are only carried along for payment-service (see {@code StockReserved}).
 */
public record ReserveStockCommand(UUID orderId, String customerEmail, BigDecimal amount, List<ReservedLine> lines) {
}
