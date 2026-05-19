package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.service.ExchangeRatesLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "file")
@RequiredArgsConstructor
public class ExchangeRatesFileLoader implements ExchangeRatesLoader {

    @Value("${loading.filePath}")
    private String loadingPath;

    /*public ExchangeRatesFileLoader(PropertiesWorker properties) {
        this.properties = properties;
    }*/

    @Override
    public List<ExchangeRate> loadRates() {
        System.out.println("Я файл");

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        File file = new File(loadingPath);

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
