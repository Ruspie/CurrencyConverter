package org.example.dto;

import java.math.BigDecimal;

public class Sum {

    private final BigDecimal sum;
    private final CurrencyCodeEnum currency;

    public Sum(BigDecimal sum, CurrencyCodeEnum currency) {
        this.sum = sum;
        this.currency = currency;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public CurrencyCodeEnum getCurrency() {
        return currency;
    }

    public void print(Sum baseSum) {
        System.out.printf("base sum = %s, converted sum = %s%n", baseSum, this);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", sum, currency.name(), currency.description());
    }

}
