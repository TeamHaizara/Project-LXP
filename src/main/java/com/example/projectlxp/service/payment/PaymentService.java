package com.example.projectlxp.service.payment;

import com.example.projectlxp.service.payment.dto.PaymentDto;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    void pay(PaymentDto dto);
}
