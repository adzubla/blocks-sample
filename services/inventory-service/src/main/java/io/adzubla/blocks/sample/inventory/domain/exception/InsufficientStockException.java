package io.adzubla.blocks.sample.inventory.domain.exception;

/** Stock can't cover a requested quantity, or the product doesn't exist. */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
