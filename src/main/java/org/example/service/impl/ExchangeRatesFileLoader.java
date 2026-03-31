package org.example.service.impl;

import org.example.config.PropertiesLoader;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.service.ExchangeRatesLoader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesFileLoader implements ExchangeRatesLoader {

    @Override
    public List<ExchangeRate> loadRates() {
        System.out.println("Я файл");

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        File file = new File(PropertiesLoader.getProperty("loading.filePath"));

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String exchangeRateLine;
            while ((exchangeRateLine = fileReader.readLine()) != null) {
                List<String> exchangeRateFields = List.of(exchangeRateLine.split(";"));
                ExchangeRate exchangeRate = new ExchangeRate(
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(0)),
                        CurrencyCodeEnum.valueOf(exchangeRateFields.get(1)),
                        new BigDecimal(exchangeRateFields.get(2)),
                        new BigDecimal(exchangeRateFields.get(3)).divide(new BigDecimal(exchangeRateFields.get(4)), 10, RoundingMode.HALF_UP));
                exchangeRates.add(exchangeRate);
            }
            ;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

}
