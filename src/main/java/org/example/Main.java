package org.example;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.schedule.ExchangeRatesLoaderScheduler;
import org.example.service.impl.CurrencyConverterImp;

import java.math.BigDecimal;

public class Main {

    public static final BigDecimal EXCHANGE_RATE_BYN_USD = BigDecimal.valueOf(2.85f);
    public static final BigDecimal EXCHANGE_RATE_BYN_EUR = BigDecimal.valueOf(3.45f);
    public static final BigDecimal EXCHANGE_RATE_BYN_RUB = BigDecimal.valueOf(2.45f);

    public static void main(String[] args) throws Exception {
        /*ExchangeRateRepositoryImpl exchangeRateRepository = null;
        try {
            List<ExchangeRateDto> exchangeRates;

            exchangeRateRepository = new ExchangeRateRepositoryImpl();
            ExchangeRateDto exchangeRateBYNEUR = new ExchangeRateDto(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, BigDecimal.valueOf(EXCHANGE_RATE_BYN_EUR), BigDecimal.valueOf(1.0));

            ExchangeRateServiceImp exchangeRateServiceImp = new ExchangeRateServiceImp(exchangeRateRepository);
            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();
            System.out.println("-----------------");
            exchangeRateServiceImp.saveExchangeRate(exchangeRateBYNEUR);
            System.out.println("-----------------");
            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();

            System.out.println("-----------------");

            exchangeRateServiceImp.deleteExchangeRate(exchangeRateBYNEUR);

            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();

            System.out.println(exchangeRates.toString());*/

        ExchangeRateDto exchangeRateBYNUSD = new ExchangeRateDto(CurrencyCodeEnum.BYN, CurrencyCodeEnum.USD, EXCHANGE_RATE_BYN_USD, BigDecimal.ONE);
        ExchangeRateDto exchangeRateBYNEUR = new ExchangeRateDto(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, EXCHANGE_RATE_BYN_EUR, BigDecimal.ONE);
        ExchangeRateDto exchangeRateBYNRUB = new ExchangeRateDto(CurrencyCodeEnum.BYN, CurrencyCodeEnum.RUB, EXCHANGE_RATE_BYN_RUB, BigDecimal.valueOf(100.0));

        //CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);
        CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp();
        ExchangeRatesLoaderScheduler exchangeRatesLoaderScheduler = new ExchangeRatesLoaderScheduler(currencyConverterImp);

        Sum baseSumBYN = new Sum(BigDecimal.valueOf(100), CurrencyCodeEnum.BYN);
        try {
            Sum sumBYNToUSD = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.USD);
            Sum sumBYNToEUR = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.EUR);
            Sum sumBYNToRUB = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.RUB);
            System.out.println();
            Sum sumUSDToBYN = currencyConverterImp.exchangeSum(sumBYNToUSD, CurrencyCodeEnum.BYN);
            Sum sumEURToBYN = currencyConverterImp.exchangeSum(sumBYNToEUR, CurrencyCodeEnum.BYN);
            Sum sumRUBToBYN = currencyConverterImp.exchangeSum(sumBYNToRUB, CurrencyCodeEnum.BYN);
            Sum sumBYNtoZL = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.ZL);
        } catch (DataNotFoundException ex) {
            System.err.println(ex);
        }

        currencyConverterImp.addExchangeRate(new ExchangeRateDto(CurrencyCodeEnum.BYN, CurrencyCodeEnum.ZL, BigDecimal.valueOf(4.8f), BigDecimal.ONE));

        try {
            Sum sumBYNtoZL = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.ZL);
        } catch (DataNotFoundException ex) {
            System.err.println(ex);
        }

        currencyConverterImp.printAllCurrencyExchangeRates();

        while (true) {

        }
       /*} finally {
            if (exchangeRateRepository != null)
                exchangeRateRepository.close();
        }*/
    }
}