package com.example.projectlxp.service.payment.strategy.impl;

import com.example.projectlxp.service.payment.strategy.ThirdPartyPayment;
import org.springframework.stereotype.Component;

@Component("KakaoPay")
public class KakaoPayPaymentStrategy implements ThirdPartyPayment {
    @Override
    public boolean pay() {
        return false;
    }
}
