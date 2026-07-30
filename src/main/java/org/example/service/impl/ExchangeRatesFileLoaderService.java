package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.service.ExchangeRatesLoaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "file")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesFileLoaderService implements ExchangeRatesLoaderService {

    @Value("${loading.filePath}")
    private String loadingPath;

    @Override
    public List<ExchangeRateDto> loadRates(LocalDate date) {
        log.debug("Я файл");

        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();

        File file = new File(loadingPath);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String exchangeRateLine;
            long i = 1L;
            while ((exchangeRateLine = fileReader.readLine()) != null) {
                List<String> exchangeRateFields = List.of(exchangeRateLine.split(";"));
                ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                        i++,
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(0)),
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(1)),
                        new BigDecimal(exchangeRateFields.get(2)),
                        new BigDecimal(exchangeRateFields.get(3)).divide(new BigDecimal(exchangeRateFields.get(4)), 10, RoundingMode.HALF_UP),
                        date
                );
                exchangeRateDtos.add(exchangeRateDto);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exchangeRateDtos;
    }

}
