package io.adzubla.blocks.sample.order.interfaces.rest;

import io.adzubla.blocks.sample.order.application.OrderNotFoundException;
import io.adzubla.blocks.sample.order.application.OrderQueries;
import io.adzubla.blocks.sample.order.application.PlaceOrderUseCase;
import io.adzubla.blocks.sample.order.application.UnknownProductException;
import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderId;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST interface of the order context.
 * <p>
 * TODO(error-handler): exceptions are translated by hand in each method below. The unified
 * error handler will map them centrally, with stable error codes, so these try/catch blocks go away.
 */
@RestController
@RequestMapping("/orders")
class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final PlaceOrderUseCase placeOrder;
    private final OrderQueries queries;

    OrderController(PlaceOrderUseCase placeOrder, OrderQueries queries) {
        this.placeOrder = placeOrder;
        this.queries = queries;
    }

    // TODO(idempotency): annotate with @Idempotent(header = Idempotent.IDEMPOTENCY_KEY_HEADER).
    // The header is accepted today but ignored, so a retried request creates a second order.
    @PostMapping
    ResponseEntity<OrderResponse> place(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PlaceOrderRequest request) {
        if (idempotencyKey != null) {
            log.debug("X-Idempotency-Key {} received but not enforced yet", idempotencyKey);
        }
        Order order;
        try {
            order = placeOrder.place(request.toCommand());
        } catch (UnknownProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.created(URI.create("/orders/" + order.id())).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    OrderResponse get(@PathVariable UUID id) {
        try {
            return OrderResponse.from(queries.get(new OrderId(id)));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping
    List<OrderResponse> list() {
        return queries.list().stream().map(OrderResponse::from).toList();
    }
}
