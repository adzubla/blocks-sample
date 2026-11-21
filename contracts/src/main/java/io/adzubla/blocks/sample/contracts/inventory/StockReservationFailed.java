package io.adzubla.blocks.sample.contracts.inventory;

import java.util.UUID;

/** Stock could not be reserved for an order. Published by inventory-service on {@code inventory.events}. */
public record StockReservationFailed(UUID orderId, String reason) {
}
