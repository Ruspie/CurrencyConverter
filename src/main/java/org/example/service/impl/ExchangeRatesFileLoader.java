package org.example.service.impl;

import org.example.config.PropertiesLoader;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.service.ExchangeRatesLoader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesFileLoader implements ExchangeRatesLoader {

    @Override
    public List<ExchangeRateDto> loadRates() {
        System.out.println("Я файл");

        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();

        File file = new File(PropertiesLoader.getProperty("loading.filePath"));

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String exchangeRateLine;
            while ((exchangeRateLine = fileReader.readLine()) != null) {
                List<String> exchangeRateFields = List.of(exchangeRateLine.split(";"));
                ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(0)),
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(1)),
                        new BigDecimal(exchangeRateFields.get(2)),
                        new BigDecimal(exchangeRateFields.get(3)).divide(new BigDecimal(exchangeRateFields.get(4)), 10, RoundingMode.HALF_UP));
                exchangeRateDtos.add(exchangeRateDto);
            }
            ;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exchangeRateDtos;
    }

}
