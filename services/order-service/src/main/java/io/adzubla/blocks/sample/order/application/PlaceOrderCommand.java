package io.adzubla.blocks.sample.order.application;

import java.util.List;

/** Input of {@link PlaceOrderUseCase}. */
public record PlaceOrderCommand(String customerEmail, List<Item> items) {

    public record Item(String sku, int quantity) {
    }
}
