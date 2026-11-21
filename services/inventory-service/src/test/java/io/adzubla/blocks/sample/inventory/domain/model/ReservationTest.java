package io.adzubla.blocks.sample.inventory.domain.model;

import io.adzubla.blocks.sample.inventory.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private final Map<String, Product> stock = new HashMap<>(Map.of(
            "KEYBOARD", new Product("KEYBOARD", "Keyboard", new BigDecimal("49.90"), 5),
            "MOUSE", new Product("MOUSE", "Mouse", new BigDecimal("19.90"), 1)));

    @Test
    void reservesEveryLine() {
        Reservation r = Reservation.reserve(UUID.randomUUID(),
                List.of(new ReservedLine("KEYBOARD", 2), new ReservedLine("MOUSE", 1)), stock);

        assertThat(r.status()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(stock.get("KEYBOARD").availableQuantity()).isEqualTo(3);
        assertThat(stock.get("MOUSE").availableQuantity()).isZero();
    }

    @Test
    void isAllOrNothing() {
        assertThatThrownBy(() -> Reservation.reserve(UUID.randomUUID(),
                List.of(new ReservedLine("KEYBOARD", 2), new ReservedLine("MOUSE", 2)), stock))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(stock.get("KEYBOARD").availableQuantity()).isEqualTo(5);
        assertThat(stock.get("MOUSE").availableQuantity()).isEqualTo(1);
    }

    @Test
    void addsUpLinesWithTheSameSku() {
        assertThatThrownBy(() -> Reservation.reserve(UUID.randomUUID(),
                List.of(new ReservedLine("MOUSE", 1), new ReservedLine("MOUSE", 1)), stock))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void rejectsUnknownProduct() {
        assertThatThrownBy(() -> Reservation.reserve(UUID.randomUUID(), List.of(new ReservedLine("NOPE", 1)), stock))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void releaseGivesStockBackOnce() {
        Reservation r = Reservation.reserve(UUID.randomUUID(), List.of(new ReservedLine("KEYBOARD", 2)), stock);

        r.release(stock);
        r.release(stock);

        assertThat(r.status()).isEqualTo(ReservationStatus.RELEASED);
        assertThat(stock.get("KEYBOARD").availableQuantity()).isEqualTo(5);
    }
}
