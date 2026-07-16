package org.example.dto;

import org.example.dto.enums.CurrencyCodeEnum;

import java.math.BigDecimal;

public class SumDto {

    private final BigDecimal sum;
    private final CurrencyCodeEnum currency;

    public SumDto(BigDecimal sum, CurrencyCodeEnum currency) {
        this.sum = sum;
        this.currency = currency;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public CurrencyCodeEnum getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", sum, currency.name(), currency.description());
    }

}
