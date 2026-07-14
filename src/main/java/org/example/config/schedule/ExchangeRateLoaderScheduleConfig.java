package org.example.config.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverterService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("exchangeRateMapper")
public class ExchangeRateLoaderScheduleConfig {

    private final CurrencyConverterService currencyConverterService;

    @PostConstruct
    public void init() {
        log.info("Загрузка курсов валют при старте сервиса");
        loadRates();
    }


    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledLoadingExchangeRates() {
        log.info("Старт расписания по загрузке курсов валют");
        loadRates();
    }

    private void loadRates() {
        try {
            currencyConverterService.loadExchangeRates();
        } catch (IOException | HttpNBRBLoaderException | InterruptedException e) {
            log.error("Ошибка загрузки кусов валют: {}", e.getMessage());
        }
    }

}
