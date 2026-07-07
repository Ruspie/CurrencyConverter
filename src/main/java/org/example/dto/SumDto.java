package org.example.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;

import java.math.BigDecimal;

@Getter
@Slf4j
public class SumDto {

    private final BigDecimal sum;
    private final CurrencyCodeEnum currency;

    public SumDto(BigDecimal sum, CurrencyCodeEnum currency) {
        this.sum = sum;
        this.currency = currency;
    }

    public void print(SumDto baseSumDto) {
        log.debug("base sum = {}, converted sum = {}", baseSumDto, this);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", sum, currency.name(), currency.description());
    }

}
