package io.adzubla.blocks.sample.inventory.domain.model;

import io.adzubla.blocks.sample.inventory.domain.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.util.Objects;

/** Aggregate root: a product in the catalog and how many units are available. */
public class Product {

    private final String sku;
    private final String name;
    private final BigDecimal price;
    private int availableQuantity;

    public Product(String sku, String name, BigDecimal price, int availableQuantity) {
        this.sku = Objects.requireNonNull(sku, "sku");
        this.name = Objects.requireNonNull(name, "name");
        this.price = Objects.requireNonNull(price, "price");
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }
        this.availableQuantity = availableQuantity;
    }

    public boolean canReserve(int quantity) {
        return quantity > 0 && quantity <= availableQuantity;
    }

    void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + sku + ": requested " + quantity + ", available " + availableQuantity);
        }
        availableQuantity -= quantity;
    }

    void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        availableQuantity += quantity;
    }

    public String sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }

    public int availableQuantity() {
        return availableQuantity;
    }
}
