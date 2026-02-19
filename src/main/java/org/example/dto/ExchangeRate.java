package org.example.dto;

import java.io.Serializable;

public class ExchangeRate implements Serializable {

    private CurrencyCodeEnum fromCurrency;
    private CurrencyCodeEnum toCurrency;
    private Double exchangeRate;
    private Double counter;

    public ExchangeRate() {}

    public ExchangeRate(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency, Double exchangeRate, Double counter) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.counter = counter;

    }

    public CurrencyCodeEnum getFromCurrency() {
        return fromCurrency;
    }

    public CurrencyCodeEnum getToCurrency() {
        return toCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public Double getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return fromCurrency.name() + " => " + toCurrency.name() + " - " +
                exchangeRate + " (1:" + String.format("%.0f", counter) + ')';
    }
}
