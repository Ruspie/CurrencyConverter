package org.example.config;

import org.example.schedule.ExchangeRatesLoaderScheduler;
import org.example.service.CurrencyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeRateLoaderSchedulerConfig {

    @Value("${scheduler.executionTime}")
    private String executionTimeString;

    @Bean("exchangeRatesLoaderScheduler")
    public ExchangeRatesLoaderScheduler initScheduler(CurrencyConverter currencyConverter) {
        ExchangeRatesLoaderScheduler exchangeRatesLoaderScheduler = new ExchangeRatesLoaderScheduler();

        exchangeRatesLoaderScheduler.initScheduler(currencyConverter, executionTimeString);

        return exchangeRatesLoaderScheduler;
    }

}
