package org.example.service.impl;

import org.example.config.PropertiesLoader;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverter;
import org.example.service.ExchangeRatesLoader;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyConverterImp implements CurrencyConverter {

    private final ExchangeRatesLoader exchangeRatesFileLoader;

    {
        if ("HTTP".equals(PropertiesLoader.getProperty("loading.mode")))
            exchangeRatesFileLoader = new HttpNBRBExchangeRatesLoader();
        else
            exchangeRatesFileLoader = new ExchangeRatesFileLoader();
    }


    public List<ExchangeRate> exchangeRates = new ArrayList<>();

    public CurrencyConverterImp(ExchangeRate... exchangeRates) {
        this.exchangeRates = Arrays.asList(exchangeRates);
    }

    public CurrencyConverterImp() throws IOException, HttpNBRBLoaderException, InterruptedException {
        loadExchangeRates();
    }

    public CurrencyConverterImp(ExchangeRate exchangeRateBYNUSD, ExchangeRate exchangeRateBYNEUR, ExchangeRate exchangeRateBYNRUB) {
        addExchangeRate(exchangeRateBYNUSD);
        addExchangeRate(exchangeRateBYNEUR);
        addExchangeRate(exchangeRateBYNRUB);

        generateAnotherExchangeRates(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);
    }

    public void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException {
        exchangeRates = exchangeRatesFileLoader.loadRates();
    }

    private void generateAnotherExchangeRates(ExchangeRate exchangeRateBYNUSD, ExchangeRate exchangeRateBYNEUR, ExchangeRate exchangeRateBYNRUB) {
        ExchangeRate exchangeRateUSDBYN = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRate exchangeRateEURBYN = new ExchangeRate(CurrencyCodeEnum.EUR, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRate exchangeRateRUBBYN = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.valueOf(1.0 / 100.0));

        addExchangeRate(exchangeRateUSDBYN);
        addExchangeRate(exchangeRateEURBYN);
        addExchangeRate(exchangeRateRUBBYN);

        ExchangeRate exchangeRateUSDEUR = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, exchangeRateUSDBYN.getExchangeRate().divide(exchangeRateEURBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRate exchangeRateEURUSD = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateUSDEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRate exchangeRateRUBUSD = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.USD, exchangeRateRUBBYN.getExchangeRate().divide(exchangeRateUSDBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRate exchangeRateUSDRUB = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateRUBUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRate exchangeRateEURRUB = new ExchangeRate(CurrencyCodeEnum.EUR, CurrencyCodeEnum.RUB, exchangeRateEURBYN.getExchangeRate().divide(exchangeRateRUBBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRate exchangeRateRUBEUR = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateEURRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        addExchangeRate(exchangeRateUSDEUR);
        addExchangeRate(exchangeRateEURUSD);
        addExchangeRate(exchangeRateRUBUSD);
        addExchangeRate(exchangeRateUSDRUB);
        addExchangeRate(exchangeRateEURRUB);
        addExchangeRate(exchangeRateRUBEUR);
    }

    public boolean addExchangeRate(ExchangeRate exchangeRate) {
        for (ExchangeRate rate : exchangeRates) {
            if (rate != null) {
                if (rate.getFromCurrency().equals(exchangeRate.getFromCurrency())
                        && rate.getToCurrency().equals(exchangeRate.getToCurrency())) {
                    rate.setExchangeRate(exchangeRate.getExchangeRate());
                    return true;
                }
            }
        }

        exchangeRates.add(exchangeRate);
        return true;
    }

    private ExchangeRate getCurrentExchangeRate(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency) {
        for (ExchangeRate rate : exchangeRates) {
            if (rate != null) {

                if (rate.getFromCurrency().equals(fromCurrency)
                        && rate.getToCurrency().equals(toCurrency)) {
                    return rate;
                }
            }
        }

        return null;
    }

    @Override
    public Sum exchangeSum(Sum sum, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException {
        ExchangeRate currentExchangeRate = getCurrentExchangeRate(sum.getCurrency(), destinationCurrency);

        if (currentExchangeRate == null) {
            throw new DataNotFoundException("Не найден курс конверсии", sum.getCurrency(), destinationCurrency);
        }

        BigDecimal result = sum.getSum()
                .divide(currentExchangeRate.getExchangeRate(), 10, RoundingMode.HALF_UP)
                .multiply(currentExchangeRate.getScale());

        Sum sumResult = new Sum(result, destinationCurrency);
        sumResult.print(sum);
        return sumResult;
    }

    @Override
    public void printAllCurrencyExchangeRates() {
        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate != null)
                System.out.println(exchangeRate);
        }
    }

}
