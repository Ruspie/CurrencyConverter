package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.dto.enums.CurrencyCodeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeRateDto implements Serializable {

    private Long id;
    @NotNull(message = "fromCurrency не может быть пустым")
    private CurrencyCodeEnum fromCurrency;
    @NotNull(message = "toCurrency не может быть пустым")
    private CurrencyCodeEnum toCurrency;
    @NotNull(message = "exchangeRate не может быть пустым")
    private BigDecimal exchangeRate;
    @NotNull(message = "scale не может быть пустым")
    private BigDecimal scale;
    @NotNull(message = "rateDate не может быть пустым")
    private LocalDate rateDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateDto that = (ExchangeRateDto) o;
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
