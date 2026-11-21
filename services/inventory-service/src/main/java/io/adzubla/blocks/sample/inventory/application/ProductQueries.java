package io.adzubla.blocks.sample.inventory.application;

import io.adzubla.blocks.sample.inventory.domain.model.Product;
import io.adzubla.blocks.sample.inventory.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductQueries {

    private final ProductRepository products;

    public ProductQueries(ProductRepository products) {
        this.products = products;
    }

    public List<Product> list() {
        return products.findAll();
    }

    public List<Product> findBySkus(Collection<String> skus) {
        return List.copyOf(products.findBySkus(skus).values());
    }
}
