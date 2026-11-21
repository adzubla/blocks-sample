package io.adzubla.blocks.sample.inventory.domain.model;

import io.adzubla.blocks.sample.inventory.domain.exception.InsufficientStockException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root: the stock held for one order. Identified by the orderId, since an order has
 * at most one reservation.
 * <p>
 * Rule: a reservation is all-or-nothing. Either every line is reserved or none is.
 */
public class Reservation {

    private final UUID orderId;
    private final List<ReservedLine> lines;
    private final Instant createdAt;
    private ReservationStatus status;

    private Reservation(UUID orderId, List<ReservedLine> lines, Instant createdAt, ReservationStatus status) {
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.lines = List.copyOf(lines);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = Objects.requireNonNull(status, "status");
    }

    /**
     * Reserves stock for every line, taking it from {@code products}.
     *
     * @param products the products the lines refer to, by SKU. They are changed in place.
     * @throws InsufficientStockException if any line can't be covered. No product is changed then.
     */
    public static Reservation reserve(UUID orderId, List<ReservedLine> lines, Map<String, Product> products) {
        // The same SKU may appear on several lines: check the total per SKU.
        Map<String, Integer> requested = new LinkedHashMap<>();
        lines.forEach(line -> requested.merge(line.sku(), line.quantity(), Integer::sum));

        // Check everything first, so a failure leaves no product half-reserved.
        requested.forEach((sku, quantity) -> {
            Product product = products.get(sku);
            if (product == null) {
                throw new InsufficientStockException("Unknown product " + sku);
            }
            if (!product.canReserve(quantity)) {
                throw new InsufficientStockException("Insufficient stock for " + sku + ": requested " + quantity
                        + ", available " + product.availableQuantity());
            }
        });
        requested.forEach((sku, quantity) -> products.get(sku).reserve(quantity));

        List<ReservedLine> merged = requested.entrySet().stream()
                .map(e -> new ReservedLine(e.getKey(), e.getValue()))
                .toList();
        return new Reservation(orderId, merged, Instant.now(), ReservationStatus.RESERVED);
    }

    public static Reservation restore(UUID orderId, List<ReservedLine> lines, Instant createdAt,
                                      ReservationStatus status) {
        return new Reservation(orderId, lines, createdAt, status);
    }

    /**
     * Gives the reserved stock back to {@code products}. Compensation for a failed payment.
     * Releasing twice does nothing the second time.
     */
    public void release(Map<String, Product> products) {
        if (status == ReservationStatus.RELEASED) {
            return;
        }
        lines.forEach(line -> products.get(line.sku()).release(line.quantity()));
        status = ReservationStatus.RELEASED;
    }

    public UUID orderId() {
        return orderId;
    }

    public List<ReservedLine> lines() {
        return lines;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public ReservationStatus status() {
        return status;
    }
}
