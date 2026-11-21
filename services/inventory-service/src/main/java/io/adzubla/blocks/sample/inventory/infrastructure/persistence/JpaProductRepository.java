package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import io.adzubla.blocks.sample.inventory.domain.model.Product;
import io.adzubla.blocks.sample.inventory.domain.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository jpa;

    JpaProductRepository(SpringDataProductRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Map<String, Product> findBySkus(Collection<String> skus) {
        return jpa.findAllById(skus).stream()
                .map(JpaProductRepository::toDomain)
                .collect(Collectors.toMap(Product::sku, Function.identity()));
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll(Sort.by("sku")).stream().map(JpaProductRepository::toDomain).toList();
    }

    @Override
    public void saveAll(Collection<Product> products) {
        jpa.saveAll(products.stream()
                .map(p -> new ProductJpaEntity(p.sku(), p.name(), p.price(), p.availableQuantity()))
                .toList());
    }

    private static Product toDomain(ProductJpaEntity e) {
        return new Product(e.getSku(), e.getName(), e.getPrice(), e.getAvailableQuantity());
    }
}
