package io.adzubla.blocks.sample.fakegateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@code POST /charges}. Rule: any charge above the limit (1000 by default) is declined.
 * Answers after a short random delay so the call is visible in traces.
 * <p>
 * Like many real providers it would accept an idempotency key. This one doesn't, so a repeated
 * request is a second charge.
 */
@RestController
class ChargeController {

    private static final Logger log = LoggerFactory.getLogger(ChargeController.class);

    private final BigDecimal limit;

    ChargeController(@Value("${gateway.limit:1000}") BigDecimal limit) {
        this.limit = limit;
    }

    @PostMapping("/charges")
    ChargeResponse charge(@RequestBody ChargeRequest request) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(50, 250));
        String transactionId = "tx_" + UUID.randomUUID();
        if (request.amount().compareTo(limit) > 0) {
            log.info("Declined {} for {}", request.amount(), request.reference());
            return new ChargeResponse(transactionId, "DECLINED", "Amount exceeds limit of " + limit);
        }
        log.info("Approved {} for {}", request.amount(), request.reference());
        return new ChargeResponse(transactionId, "APPROVED", null);
    }

    record ChargeRequest(String reference, BigDecimal amount) {
    }

    record ChargeResponse(String transactionId, String status, String reason) {
    }
}
