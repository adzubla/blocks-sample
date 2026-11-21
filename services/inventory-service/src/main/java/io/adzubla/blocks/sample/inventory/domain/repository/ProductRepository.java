package io.adzubla.blocks.sample.inventory.domain.repository;

import io.adzubla.blocks.sample.inventory.domain.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductRepository {

    /** Products with the given SKUs, by SKU. Unknown SKUs are absent from the map. */
    Map<String, Product> findBySkus(Collection<String> skus);

    List<Product> findAll();

    void saveAll(Collection<Product> products);
}
