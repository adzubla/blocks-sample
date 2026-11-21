package io.adzubla.blocks.sample.order.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class OrderLineEmbeddable {

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    protected OrderLineEmbeddable() {
    }

    OrderLineEmbeddable(String sku, int quantity, BigDecimal unitPrice) {
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    String getSku() {
        return sku;
    }

    int getQuantity() {
        return quantity;
    }

    BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
