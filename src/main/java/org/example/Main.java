package org.example;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.schedule.ExchangeRatesLoaderScheduler;
import org.example.service.impl.CurrencyConverterImp;
import org.example.service.impl.ExchangeRateServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    //public static final BigDecimal EXCHANGE_RATE_BYN_USD = BigDecimal.valueOf(2.85);
    //public static final BigDecimal EXCHANGE_RATE_BYN_EUR = BigDecimal.valueOf(3.45);
    //public static final BigDecimal EXCHANGE_RATE_BYN_RUB = BigDecimal.valueOf(2.45);

    public static void main(String[] args) throws Exception {
//        ExchangeRateRepositoryImpl exchangeRateRepository = null;
//        try {
//            List<ExchangeRate> exchangeRates;
//
//            exchangeRateRepository = new ExchangeRateRepositoryImpl();
//            ExchangeRate exchangeRateBYNEUR = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, BigDecimal.valueOf(EXCHANGE_RATE_BYN_EUR), BigDecimal.valueOf(1.0));
//
//            ExchangeRateServiceImp exchangeRateServiceImp = new ExchangeRateServiceImp(exchangeRateRepository);
//            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();
//            System.out.println("-----------------");
//            exchangeRateServiceImp.saveExchangeRate(exchangeRateBYNEUR);
//            System.out.println("-----------------");
//            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();
//
//            System.out.println("-----------------");
//
//            exchangeRateServiceImp.deleteExchangeRate(exchangeRateBYNEUR);
//
//            exchangeRates = exchangeRateServiceImp.getAllExchangeRates();
//
//            System.out.println(exchangeRates.toString());

        //ExchangeRate exchangeRateBYNUSD = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.USD, EXCHANGE_RATE_BYN_USD, BigDecimal.valueOf(1.0));
        //ExchangeRate exchangeRateBYNEUR = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.EUR, EXCHANGE_RATE_BYN_EUR, BigDecimal.valueOf(1.0));
        //ExchangeRate exchangeRateBYNRUB = new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.RUB, EXCHANGE_RATE_BYN_RUB, BigDecimal.valueOf(100.0));

        //CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp(exchangeRateBYNUSD, exchangeRateBYNEUR, exchangeRateBYNRUB);
        //CurrencyConverterImp currencyConverterImp = new CurrencyConverterImp();

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        CurrencyConverterImp currencyConverterImp = context.getBean(CurrencyConverterImp.class);
        currencyConverterImp.loadExchangeRates();
        ExchangeRateServiceImp exchangeRateServiceImp = context.getBean(ExchangeRateServiceImp.class);
        List<ExchangeRate> allExchangeRates = exchangeRateServiceImp.getAllExchangeRates();
        log.info(allExchangeRates.toString());

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

        currencyConverterImp.addExchangeRate(new ExchangeRate(CurrencyCodeEnum.BYN, CurrencyCodeEnum.ZL, BigDecimal.valueOf(4.8), BigDecimal.valueOf(1.0)));

        try {
            Sum sumBYNtoZL = currencyConverterImp.exchangeSum(baseSumBYN, CurrencyCodeEnum.ZL);
        } catch (DataNotFoundException ex) {
            System.err.println(ex);
        }

        currencyConverterImp.printAllCurrencyExchangeRates();

        // while (true) {

        // }
//        } finally {
//            if (exchangeRateRepository != null)
//                exchangeRateRepository.close();
//        }
    }
}