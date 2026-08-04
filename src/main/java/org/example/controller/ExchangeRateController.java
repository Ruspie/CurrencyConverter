package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.DataNotFoundException;
import org.example.service.CurrencyConverterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencies() {
        List<String> allCurrencies = Arrays.stream(CurrencyCodeEnum.values())
                .map(Enum::name)
                .toList();

        return new ResponseEntity<>(allCurrencies, HttpStatus.OK);
    }

    @GetMapping("/rates/dates")
    public ResponseEntity<List<LocalDate>> getAvailableDates() {
        List<LocalDate> availableDates = currencyConverterService.getAvailableDates();

        return new ResponseEntity<>(availableDates, HttpStatus.OK);
    }

    @GetMapping("/rates")
    public ResponseEntity<List<ExchangeRateDto>> getExchangeRates(@RequestParam LocalDate date) {
        List<ExchangeRateDto> allExchangeRates = currencyConverterService.getAllExchangeRates(date);

        List<ExchangeRateDto> test = new ArrayList<>();
        return new ResponseEntity<>(test, HttpStatus.OK);
    }

    @GetMapping("/convert")
    public ResponseEntity<?> exchangeSum(
            @RequestParam BigDecimal amount,
            @RequestParam(name = "from") String fromCurrency,
            @RequestParam(name = "to") String toCurrency,
            @RequestParam(name = "date") LocalDate date
    ) throws DataNotFoundException {
        SumDto sumDto = currencyConverterService.exchangeSum(
                new SumDto(amount, CurrencyCodeEnum.valueOf(fromCurrency)),
                CurrencyCodeEnum.valueOf(toCurrency),
                date
        );

        return new ResponseEntity<>(sumDto, HttpStatus.OK);
    }

}
