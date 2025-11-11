package com.example.projectlxp.service.payment.strategy.impl;

import com.example.projectlxp.service.payment.strategy.ThirdPartyPayment;
import org.springframework.stereotype.Component;

@Component("CreditCard")
public class CreditCardPaymentStrategy implements ThirdPartyPayment {
    @Override
    public boolean pay() {
        return true;
    }
}
