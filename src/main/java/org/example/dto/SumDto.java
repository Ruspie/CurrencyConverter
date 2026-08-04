package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;

import java.math.BigDecimal;

@Slf4j
@Schema(description = "Сумма в валюте")
public class SumDto {

    @Schema(description = "Сумма", example = "325.00")
    private final BigDecimal sum;

    @Schema(description = "Валюта суммы", example = "BYN")
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

    public void print(SumDto baseSumDto) {
        log.debug("base sum = {}, converted sum = {}", baseSumDto, this);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", sum, currency.name(), currency.description());
    }

}
