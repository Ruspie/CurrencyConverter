package org.example.dto;

import java.io.Serializable;
import java.util.Objects;

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

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return fromCurrency == that.fromCurrency
                && toCurrency == that.toCurrency
                && Objects.equals(exchangeRate, that.exchangeRate)
                && Objects.equals(counter, that.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, toCurrency, exchangeRate, counter);
    }

    @Override
    public String toString() {
        return fromCurrency.name() + " => " + toCurrency.name() + " - " +
                exchangeRate + " (1:" + String.format("%.0f", counter) + ')';
    }
}
