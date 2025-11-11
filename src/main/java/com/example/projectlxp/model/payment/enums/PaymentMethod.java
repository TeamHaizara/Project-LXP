package com.example.projectlxp.model.payment.enums;

public enum PaymentMethod {
    DIRECT_DEBIT("DirectDebit"),
    CREDIT_CARD("CreditCard"),
    KAKAO_PAY("KakaoPay"),
    BANK_TRANSFER("BankTransfer"),;

    private final String strategyName;
    PaymentMethod(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getStrategyName() {
        return strategyName;
    }
}
