package io.adzubla.blocks.sample.payment.application.port;

import java.math.BigDecimal;
import java.util.UUID;

/** Outbound port: the external payment provider. */
public interface PaymentGateway {

    ChargeResult charge(UUID orderId, BigDecimal amount);

    record ChargeResult(boolean approved, String transactionId, String declineReason) {
    }
}
