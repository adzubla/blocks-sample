package io.adzubla.blocks.sample.contracts.inventory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stock for every line of an order was reserved. Published by inventory-service on
 * {@code inventory.events}.
 * <p>
 * {@code customerEmail} and {@code amount} are copied from {@code OrderPlaced} only so that
 * payment-service has what it needs to charge. Passing data along every step like this is one
 * of the costs of choreography. TODO(saga): an orchestrator would send payment a command with
 * this data instead.
 */
public record StockReserved(UUID orderId, String customerEmail, BigDecimal amount) {
}
