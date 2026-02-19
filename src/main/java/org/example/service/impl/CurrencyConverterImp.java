package org.example.service.impl;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.service.CurrencyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyConverterImp implements CurrencyConverter {

    public List<ExchangeRate> exchangeRates = new ArrayList<>();

    public CurrencyConverterImp(ExchangeRate... exchangeRates) {
        exchangeRates = Arrays.stream(exchangeRates).collect(Collectors.toList());
    }

    public CurrencyConverterImp() {

    }

    public CurrencyConverterImp(ExchangeRate exchangeRateBYNUSD, ExchangeRate exchangeRateBYNEUR, ExchangeRate exchangeRateBYNRUB) {
        addExchangeRate(exchangeRateBYNUSD);
        addExchangeRate(exchangeRateBYNEUR);
        addExchangeRate(exchangeRateBYNRUB);

        generateAnotherExchangeRates(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);
    }

    private void generateAnotherExchangeRates(ExchangeRate exchangeRateBYNUSD, ExchangeRate exchangeRateBYNEUR, ExchangeRate exchangeRateBYNRUB) {
        ExchangeRate exchangeRateUSDBYN = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.BYN, 1.0 / exchangeRateBYNUSD.getExchangeRate(), 1.0);
        ExchangeRate exchangeRateEURBYN = new ExchangeRate(CurrencyCodeEnum.EUR, CurrencyCodeEnum.BYN, 1.0 / exchangeRateBYNEUR.getExchangeRate(), 1.0);
        ExchangeRate exchangeRateRUBBYN = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.BYN, 1.0 / exchangeRateBYNRUB.getExchangeRate(), 1.0 / 100.0);

        addExchangeRate(exchangeRateUSDBYN);
        addExchangeRate(exchangeRateEURBYN);
        addExchangeRate(exchangeRateRUBBYN);

        ExchangeRate exchangeRateUSDEUR = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, exchangeRateUSDBYN.getExchangeRate() / exchangeRateEURBYN.getExchangeRate(), 1.0);
        ExchangeRate exchangeRateEURUSD = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, 1.00 / exchangeRateUSDEUR.getExchangeRate(), 1.0);

        ExchangeRate exchangeRateRUBUSD = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.USD, exchangeRateRUBBYN.getExchangeRate() / exchangeRateUSDBYN.getExchangeRate(), 1.0);
        ExchangeRate exchangeRateUSDRUB = new ExchangeRate(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, 1.00 / exchangeRateRUBUSD.getExchangeRate(), 1.0);

        ExchangeRate exchangeRateEURRUB = new ExchangeRate(CurrencyCodeEnum.EUR, CurrencyCodeEnum.RUB, exchangeRateEURBYN.getExchangeRate() / exchangeRateRUBBYN.getExchangeRate(), 1.0);
        ExchangeRate exchangeRateRUBEUR = new ExchangeRate(CurrencyCodeEnum.RUB, CurrencyCodeEnum.EUR, 1.00 / exchangeRateEURRUB.getExchangeRate(), 1.0);

        addExchangeRate(exchangeRateUSDEUR);
        addExchangeRate(exchangeRateEURUSD);
        addExchangeRate(exchangeRateRUBUSD);
        addExchangeRate(exchangeRateUSDRUB);
        addExchangeRate(exchangeRateEURRUB);
        addExchangeRate(exchangeRateRUBEUR);
    }

    public boolean addExchangeRate(ExchangeRate exchangeRate) {
        int counter = 0;

        for (ExchangeRate rate : exchangeRates) {
            if (rate != null) {
                counter++;

                if (rate.getFromCurrency().equals(exchangeRate.getFromCurrency())
                        && rate.getToCurrency().equals(exchangeRate.getToCurrency())) {
                    rate = exchangeRate;
                    return true;
                }
            }
        }

        if (counter > exchangeRates.length) {
            return false;
            //throw new IllegalArgumentException("Limit currency exchange rates");
        }

        exchangeRates[counter] = exchangeRate;
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

        double result = sum.getSum() / currentExchangeRate.getExchangeRate() * currentExchangeRate.getCounter();

        Sum sumResult = new Sum(Math.round(result * 100.0) / 100.0, destinationCurrency);
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
