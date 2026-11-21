package io.adzubla.blocks.sample.inventory.interfaces.rest;

import io.adzubla.blocks.sample.inventory.domain.model.Product;

import java.math.BigDecimal;

public record ProductResponse(String sku, String name, BigDecimal price, int availableQuantity) {

    static ProductResponse from(Product p) {
        return new ProductResponse(p.sku(), p.name(), p.price(), p.availableQuantity());
    }
}
