package org.example.schedule;

import org.example.config.PropertiesLoader;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExchangeRatesLoaderScheduler {

    public ExchangeRatesLoaderScheduler(CurrencyConverter currencyConverter) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        LocalTime executionTime = LocalTime.parse(PropertiesLoader.getProperty("scheduler.executionTime"), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime now = LocalTime.now();

        long initialDelay = Duration.between(now, executionTime).toMillis();
        if (initialDelay < 0)
            initialDelay += Duration.ofDays(1).toMillis();

        executorService.scheduleAtFixedRate(() -> {
            try {
                currencyConverter.loadExchangeRates();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (HttpNBRBLoaderException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, initialDelay, Duration.ofDays(1).toMillis(), TimeUnit.MILLISECONDS);
    }

}
