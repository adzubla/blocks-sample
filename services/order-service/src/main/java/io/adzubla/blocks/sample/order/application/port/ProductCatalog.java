package io.adzubla.blocks.sample.order.application.port;

import io.adzubla.blocks.sample.order.domain.model.Money;

import java.util.Map;
import java.util.Set;

/** Outbound port: current product prices. Implemented by a REST client to inventory-service. */
public interface ProductCatalog {

    /** Returns the unit price of each SKU that exists. Unknown SKUs are absent from the map. */
    Map<String, Money> findPrices(Set<String> skus);
}
