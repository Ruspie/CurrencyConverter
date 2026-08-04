package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.ErrorResponseDto;
import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.DataNotFoundException;
import org.example.service.CurrencyConverterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Exchange Rates", description = "Публичные операции с курсами валют и конвертацией")
public class ExchangeRateController {

    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/currencies")
    @Operation(summary = "Список валют", description = "Возвращает поддерживаемые коды валют")
    @ApiResponse(responseCode = "200", description = "Список кодов валют",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string", example = "USD"))))
    public ResponseEntity<List<String>> getCurrencies() {
        List<String> allCurrencies = Arrays.stream(CurrencyCodeEnum.values())
                .map(Enum::name)
                .toList();

        return new ResponseEntity<>(allCurrencies, HttpStatus.OK);
    }

    @GetMapping("/rates/dates")
    @Operation(summary = "Доступные даты курсов", description = "Возвращает даты, для которых есть курсы валют")
    @ApiResponse(responseCode = "200", description = "Список дат",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "date", example = "2026-08-04"))))
    public ResponseEntity<List<LocalDate>> getAvailableDates() {
        List<LocalDate> availableDates = currencyConverterService.getAvailableDates();

        return new ResponseEntity<>(availableDates, HttpStatus.OK);
    }

    @GetMapping("/rates")
    @Operation(summary = "Курсы на дату", description = "Возвращает курсы валют на указанную дату")
    @ApiResponse(responseCode = "200", description = "Список курсов",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExchangeRateDto.class))))
    public ResponseEntity<List<ExchangeRateDto>> getExchangeRates(
            @Parameter(description = "Дата курсов", required = true, example = "2026-08-04")
            @RequestParam LocalDate date
    ) {
        List<ExchangeRateDto> allExchangeRates = currencyConverterService.getAllExchangeRates(date);

        List<ExchangeRateDto> test = new ArrayList<>();
        return new ResponseEntity<>(test, HttpStatus.OK);
    }

    @GetMapping("/convert")
    @Operation(summary = "Конвертация суммы", description = "Конвертирует сумму из одной валюты в другую на указанную дату")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результат конвертации",
                    content = @Content(schema = @Schema(implementation = SumDto.class))),
            @ApiResponse(responseCode = "400", description = "Курс не найден или некорректные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<?> exchangeSum(
            @Parameter(description = "Сумма для конвертации", required = true, example = "100.00")
            @RequestParam BigDecimal amount,
            @Parameter(description = "Исходная валюта", required = true, example = "USD")
            @RequestParam(name = "from") String fromCurrency,
            @Parameter(description = "Целевая валюта", required = true, example = "BYN")
            @RequestParam(name = "to") String toCurrency,
            @Parameter(description = "Дата курса", required = true, example = "2026-08-04")
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
