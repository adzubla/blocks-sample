package io.adzubla.blocks.sample.payment.application;

import io.adzubla.blocks.sample.payment.application.port.PaymentEventPublisher;
import io.adzubla.blocks.sample.payment.application.port.PaymentGateway;
import io.adzubla.blocks.sample.payment.domain.model.Payment;
import io.adzubla.blocks.sample.payment.domain.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Use case: charge the customer once their stock is reserved, and report the outcome.
 * <p>
 * Not {@code @Transactional} on purpose: holding a DB transaction open during a remote call is
 * a bad idea. The price is that the charge, the saved payment and the published event are three
 * separate steps, and a crash between them loses information:
 * <ul>
 *   <li>charged but not saved: the customer paid and nobody knows. When the message is
 *       redelivered, the customer is charged again.</li>
 *   <li>saved but not published: the order stays PENDING forever. TODO(outbox).</li>
 * </ul>
 */
@Service
public class ChargePaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChargePaymentUseCase.class);

    private final PaymentGateway gateway;
    private final PaymentRepository payments;
    private final PaymentEventPublisher events;

    public ChargePaymentUseCase(PaymentGateway gateway, PaymentRepository payments, PaymentEventPublisher events) {
        this.gateway = gateway;
        this.payments = payments;
        this.events = events;
    }

    public void charge(UUID orderId, BigDecimal amount) {
        // TODO(idempotency): a redelivered StockReserved charges the customer again. Dedupe the
        // delivery, and send the gateway an idempotency key (the orderId) so it can dedupe too.
        PaymentGateway.ChargeResult result = gateway.charge(orderId, amount);

        Payment payment = result.approved()
                ? Payment.approved(orderId, amount, result.transactionId())
                : Payment.declined(orderId, amount, result.transactionId(), result.declineReason());
        payments.save(payment);
        log.info("Payment {} for order {}: {}", payment.id(), orderId, payment.status());

        if (payment.isApproved()) {
            events.paymentCompleted(payment);
        } else {
            events.paymentFailed(payment);
        }
    }
}
