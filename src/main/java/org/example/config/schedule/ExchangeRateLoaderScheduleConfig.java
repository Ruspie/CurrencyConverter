package org.example.config.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "loading.enable", havingValue = "true")
public class ExchangeRateLoaderScheduleConfig {

    private final CurrencyConverterService currencyConverterService;

    @Value("${loading.history-days:10}")
    private int historyDays;

    @PostConstruct
    public void init() {
        log.info("Загрузка курсов валют за последние {} дней", historyDays);
        LocalDate today = LocalDate.now();
        for (int daysBack = Math.max(1, historyDays) - 1; daysBack >= 0; daysBack--) {
            loadRates(today.minusDays(daysBack));
        }
    }


    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledLoadingExchangeRates() {
        log.info("Старт расписания по загрузке курсов валют");
        loadRates(LocalDate.now());
    }

    private void loadRates(LocalDate rateDate) {
        try {
            currencyConverterService.loadExchangeRates(rateDate);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.error("Загрузка курсов валют за {} прервана", rateDate, exception);
        } catch (IOException | HttpNBRBLoaderException | RuntimeException exception) {
            log.error("Ошибка загрузки курсов валют за {}", rateDate, exception);
        }
    }

}
