package io.adzubla.blocks.sample.payment.infrastructure.gateway;

import io.adzubla.blocks.sample.payment.application.port.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adapter: the payment provider's REST API (fake-payment-gateway in this sample).
 * <p>
 * Phase 1: if the gateway is down or slow, the exception goes up to the Kafka listener, which
 * retries the whole message, calling the gateway again each time.
 */
@Component
class RestPaymentGateway implements PaymentGateway {

    private final RestClient restClient;

    RestPaymentGateway(RestClient.Builder builder, @Value("${app.payment-gateway.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public ChargeResult charge(UUID orderId, BigDecimal amount) {
        ChargeResponse response = restClient.post()
                .uri("/charges")
                .body(new ChargeRequest(orderId.toString(), amount))
                .retrieve()
                .body(ChargeResponse.class);
        if (response == null) {
            throw new IllegalStateException("Empty response from payment gateway");
        }
        return new ChargeResult("APPROVED".equals(response.status()), response.transactionId(), response.reason());
    }

    record ChargeRequest(String reference, BigDecimal amount) {
    }

    record ChargeResponse(String transactionId, String status, String reason) {
    }
}
