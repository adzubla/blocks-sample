package io.adzubla.blocks.sample.payment.interfaces.rest;

import io.adzubla.blocks.sample.payment.application.PaymentQueries;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** REST interface: read-only view of payments, for observing the system. */
@RestController
@RequestMapping("/payments")
class PaymentController {

    private final PaymentQueries queries;

    PaymentController(PaymentQueries queries) {
        this.queries = queries;
    }

    @GetMapping
    List<PaymentResponse> list(@RequestParam(required = false) UUID orderId) {
        return queries.list(orderId).stream().map(PaymentResponse::from).toList();
    }
}
