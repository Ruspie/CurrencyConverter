package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.DataNotFoundException;
import org.example.service.CurrencyConverterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencies() {
        List<String> currencies = Arrays.stream(CurrencyCodeEnum.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/rates/dates")
    public ResponseEntity<List<LocalDate>> getAvailableRateDates() {
        return ResponseEntity.ok(currencyConverterService.getAvailableRateDates());
    }

    @GetMapping("/rates")
    public ResponseEntity<List<ExchangeRateDto>> getExchangeRates(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(currencyConverterService.getExchangeRates(date));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/convert")
    public ResponseEntity<?> exchangeSum(
            @RequestParam BigDecimal amount,
            @RequestParam(name = "from") String fromCurrency,
            @RequestParam(name = "to") String toCurrency,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) throws DataNotFoundException {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше нуля");
        }

        CurrencyCodeEnum from = parseCurrency(fromCurrency);
        CurrencyCodeEnum to = parseCurrency(toCurrency);
        SumDto sumDto = currencyConverterService.exchangeSum(new SumDto(amount, from), to, date);

        return ResponseEntity.ok(sumDto);
    }

    private CurrencyCodeEnum parseCurrency(String currency) {
        try {
            return CurrencyCodeEnum.valueOf(currency.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Неподдерживаемая валюта: " + currency);
        }
    }

}
