package org.example.service.impl;

import org.example.config.PropertiesLoader;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
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

    private List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();

    public CurrencyConverterImp(ExchangeRateDto... exchangeRateDtos) {
        this.exchangeRateDtos = Arrays.asList(exchangeRateDtos);
    }

    public CurrencyConverterImp() throws IOException, HttpNBRBLoaderException, InterruptedException {
        loadExchangeRates();
    }

    public CurrencyConverterImp(ExchangeRateDto exchangeRateDtoBYNUSD, ExchangeRateDto exchangeRateDtoBYNEUR, ExchangeRateDto exchangeRateDtoBYNRUB) {
        addExchangeRate(exchangeRateDtoBYNUSD);
        addExchangeRate(exchangeRateDtoBYNEUR);
        addExchangeRate(exchangeRateDtoBYNRUB);

        generateAnotherExchangeRates(exchangeRateDtoBYNUSD, exchangeRateDtoBYNEUR, exchangeRateDtoBYNRUB);
    }

    public void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException {
        exchangeRateDtos = exchangeRatesFileLoader.loadRates();
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRates() {
        return exchangeRateDtos;
    }

    private void generateAnotherExchangeRates(ExchangeRateDto exchangeRateDtoBYNUSD, ExchangeRateDto exchangeRateDtoBYNEUR, ExchangeRateDto exchangeRateDtoBYNRUB) {
        ExchangeRateDto exchangeRateDtoUSDBYN = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateDtoBYNUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateDtoEURBYN = new ExchangeRateDto(CurrencyCodeEnum.EUR, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateDtoBYNEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateDtoRUBBYN = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateDtoBYNRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.valueOf(1.0 / 100.0));

        addExchangeRate(exchangeRateDtoUSDBYN);
        addExchangeRate(exchangeRateDtoEURBYN);
        addExchangeRate(exchangeRateDtoRUBBYN);

        ExchangeRateDto exchangeRateDtoUSDEUR = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, exchangeRateDtoUSDBYN.getExchangeRate().divide(exchangeRateDtoEURBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateDtoEURUSD = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateDtoUSDEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRateDto exchangeRateDtoRUBUSD = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.USD, exchangeRateDtoRUBBYN.getExchangeRate().divide(exchangeRateDtoUSDBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateDtoUSDRUB = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateDtoRUBUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRateDto exchangeRateDtoEURRUB = new ExchangeRateDto(CurrencyCodeEnum.EUR, CurrencyCodeEnum.RUB, exchangeRateDtoEURBYN.getExchangeRate().divide(exchangeRateDtoRUBBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateDtoRUBEUR = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateDtoEURRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        addExchangeRate(exchangeRateDtoUSDEUR);
        addExchangeRate(exchangeRateDtoEURUSD);
        addExchangeRate(exchangeRateDtoRUBUSD);
        addExchangeRate(exchangeRateDtoUSDRUB);
        addExchangeRate(exchangeRateDtoEURRUB);
        addExchangeRate(exchangeRateDtoRUBEUR);
    }

    public boolean addExchangeRate(ExchangeRateDto exchangeRateDto) {
        for (ExchangeRateDto rate : exchangeRateDtos) {
            if (rate != null) {
                if (rate.getFromCurrency().equals(exchangeRateDto.getFromCurrency())
                        && rate.getToCurrency().equals(exchangeRateDto.getToCurrency())) {
                    rate.setExchangeRate(exchangeRateDto.getExchangeRate());
                    return true;
                }
            }
        }

        exchangeRateDtos.add(exchangeRateDto);
        return true;
    }

    private ExchangeRateDto getCurrentExchangeRate(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency) {
        for (ExchangeRateDto rate : exchangeRateDtos) {
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
        ExchangeRateDto currentExchangeRateDto = getCurrentExchangeRate(sum.getCurrency(), destinationCurrency);

        if (currentExchangeRateDto == null) {
            throw new DataNotFoundException("Не найден курс конверсии", sum.getCurrency(), destinationCurrency);
        }

        BigDecimal result = sum.getSum()
                .divide(currentExchangeRateDto.getExchangeRate(), 10, RoundingMode.HALF_UP)
                .multiply(currentExchangeRateDto.getScale());

        Sum sumResult = new Sum(result, destinationCurrency);
        sumResult.print(sum);
        return sumResult;
    }

    @Override
    public void printAllCurrencyExchangeRates() {
        for (ExchangeRateDto exchangeRateDto : exchangeRateDtos) {
            if (exchangeRateDto != null)
                System.out.println(exchangeRateDto);
        }
    }

}
