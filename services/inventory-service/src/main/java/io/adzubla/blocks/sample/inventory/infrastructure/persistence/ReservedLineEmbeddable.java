package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReservedLineEmbeddable {

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    protected ReservedLineEmbeddable() {
    }

    ReservedLineEmbeddable(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    String getSku() {
        return sku;
    }

    int getQuantity() {
        return quantity;
    }
}
