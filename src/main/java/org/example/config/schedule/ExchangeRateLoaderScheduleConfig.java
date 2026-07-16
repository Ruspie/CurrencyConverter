package org.example.config.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn({"exchangeRateMapper", "flyway"})
public class ExchangeRateLoaderScheduleConfig {

    @Value("${loading.history.days}")
    private Long historyDays;

    private final CurrencyConverterService currencyConverterService;

    @PostConstruct
    public void init() {
        log.info("Загрузка курсов валют при старте сервиса");
        LocalDate date = LocalDate.now();
        for (int daysBack = 0; daysBack < historyDays; daysBack++) {
            loadRates(date.minusDays(daysBack));
        }
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledLoadingExchangeRates() {
        log.info("Старт расписания по загрузке курсов валют");
        loadRates(LocalDate.now());
    }

    private void loadRates(LocalDate date) {
        try {
            currencyConverterService.loadExchangeRates(date);
        } catch (IOException | HttpNBRBLoaderException | InterruptedException e) {
            log.error("Ошибка загрузки кусов валют: {}", e.getMessage());
        }
    }

}
