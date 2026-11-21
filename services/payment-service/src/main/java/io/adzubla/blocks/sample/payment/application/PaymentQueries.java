package io.adzubla.blocks.sample.payment.application;

import io.adzubla.blocks.sample.payment.domain.model.Payment;
import io.adzubla.blocks.sample.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentQueries {

    private final PaymentRepository payments;

    public PaymentQueries(PaymentRepository payments) {
        this.payments = payments;
    }

    public List<Payment> list(UUID orderId) {
        return orderId == null ? payments.findAll() : payments.findByOrderId(orderId);
    }
}
