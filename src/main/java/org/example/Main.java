package org.example;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.repository.imp.ExchangeRateRepositoryImpl;
import org.example.service.impl.ExchangeRateServiceImp;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static final double EXCHANGE_RATE_BYN_USD = 2.85;
    public static final double EXCHANGE_RATE_BYN_EUR = 3.45;
    public static final double EXCHANGE_RATE_BYN_RUB = 2.45;

    public static void main(String[] args) throws Exception {
        ExchangeRateRepositoryImpl exchangeRateRepository = null;
        try {
            List<ExchangeRate> exchangeRates;

            exchangeRateRepository = new ExchangeRateRepositoryImpl();
            ExchangeRate exchangeRateBYNEUR = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, BigDecimal.valueOf(EXCHANGE_RATE_BYN_EUR), BigDecimal.valueOf(1.0));

            ExchangeRateServiceImp exchangeRateServiceImp = new ExchangeRateServiceImp(exchangeRateRepository);
            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();
            System.out.println("-----------------");
            exchangeRateServiceImp.saveExchangeRate(exchangeRateBYNEUR);
            System.out.println("-----------------");
            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();

            System.out.println("-----------------");

            exchangeRateServiceImp.deleteExchangeRate(exchangeRateBYNEUR);

            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();

            System.out.println(exchangeRates.toString());

        /*ExchangeRate exchangeRateBYNUSD = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.USD, EXCHANGE_RATE_BYN_USD, 1.0);
        ExchangeRate exchangeRateBYNEUR = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, EXCHANGE_RATE_BYN_EUR, 1.0);
        ExchangeRate exchangeRateBYNRUB = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.RUB, EXCHANGE_RATE_BYN_RUB, 100.0);

        //CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);
        CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp();
        ExchangeRatesLoaderScheduler exchangeRatesLoaderScheduler = new ExchangeRatesLoaderScheduler(currencyConverterImp);

        Sum baseSumBYN = new Sum(100, CurrencyCodeEnum.BYN);
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

        currencyConverterImp.addExchangeRate(new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.ZL, 4.8, 1.0));

        try {
            Sum sumBYNtoZL = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.ZL);
        } catch (DataNotFoundException ex) {
            System.err.println(ex);
        }

        currencyConverterImp.printAllCurrencyExchangeRates();

        while (true) {

        }*/
        } finally {
            if (exchangeRateRepository != null)
                exchangeRateRepository.close();
        }
    }
}