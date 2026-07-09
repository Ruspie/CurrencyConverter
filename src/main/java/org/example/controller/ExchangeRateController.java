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
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/currencies")
    public ResponseEntity<?> getCurrencies() {
        List<ExchangeRateDto> allExchangeRates = currencyConverterService.getAllExchangeRates();

        return new ResponseEntity<>(allExchangeRates, HttpStatus.OK);
    }

    @GetMapping("/rates")
    public ResponseEntity<?> getExchangeRates() {
        List<ExchangeRateDto> allExchangeRates = currencyConverterService.getAllExchangeRates();

        return new ResponseEntity<>(allExchangeRates, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/convert")
    public ResponseEntity<?> exchangeSum(
            @RequestParam BigDecimal amount,
            @RequestParam(name = "from") String fromCurrency,
            @RequestParam(name = "to") String toCurrency
    ) throws DataNotFoundException {
        SumDto sumDto = currencyConverterService.exchangeSum(new SumDto(amount, CurrencyCodeEnum.valueOf(fromCurrency)), CurrencyCodeEnum.valueOf(toCurrency));

        return new ResponseEntity<>(sumDto, HttpStatus.OK);
    }

}
