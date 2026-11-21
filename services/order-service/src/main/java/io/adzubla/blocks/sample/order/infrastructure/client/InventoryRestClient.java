package io.adzubla.blocks.sample.order.infrastructure.client;

import io.adzubla.blocks.sample.order.application.port.ProductCatalog;
import io.adzubla.blocks.sample.order.domain.model.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter: reads prices from inventory-service over REST. Synchronous on purpose: placing an
 * order needs the prices right away.
 * <p>
 * Synchronous coupling: if inventory-service is down, placing an order fails. Phase 1 has no
 * timeout tuning, retries, circuit breaker or local copy of the catalog.
 */
@Component
class InventoryRestClient implements ProductCatalog {

    private final RestClient restClient;

    InventoryRestClient(RestClient.Builder builder, @Value("${app.inventory.base-url}") String baseUrl) {
        // The Boot-provided builder is instrumented, so the trace continues into inventory-service.
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Map<String, Money> findPrices(Set<String> skus) {
        ProductResponse[] products = restClient.get()
                .uri(uri -> uri.path("/products").queryParam("skus", String.join(",", skus)).build())
                .retrieve()
                .body(ProductResponse[].class);
        return Arrays.stream(products == null ? new ProductResponse[0] : products)
                .collect(Collectors.toMap(ProductResponse::sku, p -> Money.of(p.price())));
    }

    /** inventory-service's response, as this service sees it. Only the fields it needs. */
    record ProductResponse(String sku, BigDecimal price) {
    }
}
