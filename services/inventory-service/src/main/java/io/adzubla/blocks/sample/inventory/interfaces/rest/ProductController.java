package io.adzubla.blocks.sample.inventory.interfaces.rest;

import io.adzubla.blocks.sample.inventory.application.ProductQueries;
import io.adzubla.blocks.sample.inventory.domain.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST interface: the product catalog. Queried by order-service to price orders. */
@RestController
@RequestMapping("/products")
class ProductController {

    private final ProductQueries queries;

    ProductController(ProductQueries queries) {
        this.queries = queries;
    }

    /** All products, or only those in {@code ?skus=A,B}. */
    @GetMapping
    List<ProductResponse> list(@RequestParam(required = false) List<String> skus) {
        List<Product> products = skus == null || skus.isEmpty() ? queries.list() : queries.findBySkus(skus);
        return products.stream().map(ProductResponse::from).toList();
    }
}
