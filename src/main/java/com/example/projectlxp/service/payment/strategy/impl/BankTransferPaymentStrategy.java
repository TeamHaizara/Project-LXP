package com.example.projectlxp.service.payment.strategy.impl;

import com.example.projectlxp.service.payment.strategy.ThirdPartyPayment;
import org.springframework.stereotype.Component;

@Component("BankTransfer")
public class BankTransferPaymentStrategy implements ThirdPartyPayment {
    @Override
    public boolean pay() {
        return true;
    }
}
