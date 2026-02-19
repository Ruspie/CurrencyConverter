package org.example.dto;

public class Sum {

    private final double sum;
    private final CurrencyCodeEnum currency;

    public Sum(double sum, CurrencyCodeEnum currency) {
        this.sum = sum;
        this.currency = currency;
    }

    public double getSum() {
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
