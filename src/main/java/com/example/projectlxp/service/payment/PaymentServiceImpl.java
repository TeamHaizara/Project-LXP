package com.example.projectlxp.service.payment;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.repository.payment.PaymentRepository;
import com.example.projectlxp.service.payment.dto.PaymentDto;
import com.example.projectlxp.service.payment.strategy.ThirdPartyPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final Map<String, ThirdPartyPayment> thirdPartyPaymentStrategies;

    public PaymentServiceImpl(
        PaymentRepository paymentRepository,
        Map<String, ThirdPartyPayment> thirdPartyPaymentStrategies
    ) {
        this.paymentRepository = paymentRepository;
        this.thirdPartyPaymentStrategies = thirdPartyPaymentStrategies;
    }

    @Override
    @Transactional
    public void pay(PaymentDto dto) {
        ThirdPartyPayment strategy = thirdPartyPaymentStrategies.get(dto.paymentMethod().getStrategyName());
        boolean result = strategy.pay();

        if (!result) {
            throw BusinessException.builder(ExceptionCode.PAYMENT_FAILED).build();
        }

        paymentRepository.save(dto.toEntity());
    }
}
