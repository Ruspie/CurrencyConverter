package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Курс обмена валют")
public class ExchangeRateDto implements Serializable {

    @Schema(description = "Идентификатор курса", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "fromCurrency не может быть пустым")
    @Schema(description = "Исходная валюта", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    private CurrencyCodeEnum fromCurrency;

    @NotNull(message = "toCurrency не может быть пустым")
    @Schema(description = "Целевая валюта", example = "BYN", requiredMode = Schema.RequiredMode.REQUIRED)
    private CurrencyCodeEnum toCurrency;

    @NotNull(message = "exchangeRate не может быть пустым")
    @Schema(description = "Значение курса", example = "3.2500", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal exchangeRate;

    @NotNull(message = "scale не может быть пустым")
    @Schema(description = "Масштаб курса", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal scale;

    @NotNull(message = "rateDate не может быть пустым")
    @Schema(description = "Дата курса", example = "2026-08-04", requiredMode = Schema.RequiredMode.REQUIRED)
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
