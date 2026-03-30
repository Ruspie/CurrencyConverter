package org.example.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.Objects;

@Builder
public class ExchangeRate implements Serializable {

    private CurrencyCodeEnum fromCurrency;
    private CurrencyCodeEnum toCurrency;
    private Double exchangeRate;
    private Double scale;

    public ExchangeRate() {}

    public ExchangeRate(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency, Double exchangeRate, Double scale) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.scale = scale;

    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setFromCurrency(CurrencyCodeEnum fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToCurrency(CurrencyCodeEnum toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setScale(Double scale) {
        this.scale = scale;
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

    public Double getScale() {
        return scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return fromCurrency == that.fromCurrency
                && toCurrency == that.toCurrency
                && Objects.equals(exchangeRate, that.exchangeRate)
                && Objects.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, toCurrency, exchangeRate, scale);
    }

    @Override
    public String toString() {
        return fromCurrency.name() + " => " + toCurrency.name() + " - " +
                exchangeRate + " (1:" + String.format("%.0f", scale) + ')';
    }
}
