package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    // Phase 1: no @Version, so two concurrent reservations of the same product could
    // overwrite each other. Each service consumes with a single thread, which hides this.
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    protected ProductJpaEntity() {
    }

    ProductJpaEntity(String sku, String name, BigDecimal price, int availableQuantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    String getSku() {
        return sku;
    }

    String getName() {
        return name;
    }

    BigDecimal getPrice() {
        return price;
    }

    int getAvailableQuantity() {
        return availableQuantity;
    }
}
