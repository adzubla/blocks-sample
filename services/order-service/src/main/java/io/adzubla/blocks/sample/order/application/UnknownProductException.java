package io.adzubla.blocks.sample.order.application;

import java.util.Set;

/** The order refers to SKUs that the catalog doesn't know. */
public class UnknownProductException extends RuntimeException {

    public UnknownProductException(Set<String> skus) {
        super("Unknown products: " + skus);
    }
}
