package io.adzubla.blocks.sample.order.domain.model;

import io.adzubla.blocks.sample.order.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static Order newOrder() {
        return Order.place("ana@example.com", List.of(
                new OrderLine("KEYBOARD", 2, Money.of(new BigDecimal("49.90"))),
                new OrderLine("MOUSE", 1, Money.of(new BigDecimal("19.90")))));
    }

    @Test
    void newOrderIsPendingAndTotalsItsLines() {
        Order order = newOrder();

        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.total()).isEqualTo(Money.of(new BigDecimal("119.70")));
    }

    @Test
    void orderNeedsAtLeastOneLine() {
        assertThatThrownBy(() -> Order.place("ana@example.com", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pendingOrderCanBeConfirmed() {
        Order order = newOrder();

        order.confirm();

        assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void pendingOrderCanBeCancelledWithReason() {
        Order order = newOrder();

        order.cancel("out of stock");

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.cancellationReason()).isEqualTo("out of stock");
    }

    @Test
    void settledOrderCannotChangeAgain() {
        Order order = newOrder();
        order.confirm();

        assertThatThrownBy(order::confirm).isInstanceOf(InvalidOrderStateException.class);
        assertThatThrownBy(() -> order.cancel("late")).isInstanceOf(InvalidOrderStateException.class);
    }
}
