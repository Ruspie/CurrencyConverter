package org.example.schedule;

import lombok.RequiredArgsConstructor;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ExchangeRatesLoaderScheduler {

    public void initScheduler(CurrencyConverter currencyConverter, String executionTimeString) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        LocalTime executionTime = LocalTime.parse(executionTimeString, DateTimeFormatter.ofPattern("HH:mm"));
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
