package org.example;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.service.impl.CurrencyConverterImp;

public class Main {

    public static final double EXCHANGE_RATE_BYN_USD = 2.85;
    public static final double EXCHANGE_RATE_BYN_EUR = 3.45;
    public static final double EXCHANGE_RATE_BYN_RUB = 2.45;

    public static void main(String[] args) {

        ExchangeRate exchangeRateBYNUSD = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.USD, EXCHANGE_RATE_BYN_USD, 1.0);
        ExchangeRate exchangeRateBYNEUR = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, EXCHANGE_RATE_BYN_EUR, 1.0);
        ExchangeRate exchangeRateBYNRUB = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.RUB, EXCHANGE_RATE_BYN_RUB, 100.0);

        CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);

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

    }
}