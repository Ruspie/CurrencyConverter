package org.example.dto;

import lombok.Builder;
import org.example.dto.enums.CurrencyCodeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Builder
public class ExchangeRateDto implements Serializable {

    private CurrencyCodeEnum fromCurrency;
    private CurrencyCodeEnum toCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal scale;
    private LocalDate rateDate;

    public ExchangeRateDto() {}

    public ExchangeRateDto(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency, BigDecimal exchangeRate, BigDecimal scale) {
        this(fromCurrency, toCurrency, exchangeRate, scale, LocalDate.now());
    }

    public ExchangeRateDto(
            CurrencyCodeEnum fromCurrency,
            CurrencyCodeEnum toCurrency,
            BigDecimal exchangeRate,
            BigDecimal scale,
            LocalDate rateDate
    ) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.scale = scale;
        this.rateDate = rateDate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setFromCurrency(CurrencyCodeEnum fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToCurrency(CurrencyCodeEnum toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public CurrencyCodeEnum getFromCurrency() {
        return fromCurrency;
    }

    public CurrencyCodeEnum getToCurrency() {
        return toCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDate rateDate) {
        this.rateDate = rateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateDto that = (ExchangeRateDto) o;
        return fromCurrency == that.fromCurrency
                && toCurrency == that.toCurrency
                && Objects.equals(exchangeRate, that.exchangeRate)
                && Objects.equals(scale, that.scale)
                && Objects.equals(rateDate, that.rateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, toCurrency, exchangeRate, scale, rateDate);
    }

    @Override
    public String toString() {
        return fromCurrency.name() + " => " + toCurrency.name() + " - " +
                exchangeRate + " (1:" + String.format("%.0f", scale) + "), " + rateDate;
    }
}
