package org.example.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {

    private final CurrencyConverterService currencyConverterService;

    @PostConstruct
    public void onStartup() {
        log.info("Загрузка курсов при старте приложения");
        loadRates();
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledLoad() {
        log.info("Загрузка курсов по расписанию");
        loadRates();
    }

    private void loadRates() {
        try {
            currencyConverterService.loadExchangeRates();
            log.info("Курсы успешно загружены");
        } catch (IOException | HttpNBRBLoaderException | InterruptedException e) {
            log.error("Ошибка загрузки курсов: {}", e.getMessage());
        }
    }

}
