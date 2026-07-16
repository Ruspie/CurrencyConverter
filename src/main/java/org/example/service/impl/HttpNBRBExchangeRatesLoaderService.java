package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRateDto;
import org.example.exception.HttpNBRBLoaderException;
import org.example.repository.entity.mapper.ExchangeRateMapper;
import org.example.service.ExchangeRatesLoaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "http")
@Slf4j
public class HttpNBRBExchangeRatesLoaderService implements ExchangeRatesLoaderService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExchangeRateMapper exchangeRateMapper;
    private final String ratesUrl;
    private final Duration requestTimeout;
    private final int maxAttempts;

    public HttpNBRBExchangeRatesLoaderService(
            ObjectMapper objectMapper,
            ExchangeRateMapper exchangeRateMapper,
            @Value("${loading.http.url:https://api.nbrb.by/exrates/rates?periodicity=0}") String ratesUrl,
            @Value("${loading.http.timeout:10s}") Duration requestTimeout,
            @Value("${loading.http.max-attempts:3}") int maxAttempts
    ) {
        this.objectMapper = objectMapper;
        this.exchangeRateMapper = exchangeRateMapper;
        this.ratesUrl = ratesUrl;
        this.requestTimeout = requestTimeout;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(requestTimeout)
                .build();
    }

    @Override
    @SuppressWarnings("null")
    public List<ExchangeRateDto> loadRates(LocalDate date)
            throws IOException, InterruptedException, HttpNBRBLoaderException {
        String separator = ratesUrl.contains("?") ? "&" : "?";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ratesUrl + separator + "ondate=" + date))
                .timeout(requestTimeout)
                .GET()
                .build();

        HttpResponse<String> response = sendWithRetry(request);

        List<NBRBExchangeRateDto> nbrbExchangeRateDtos = objectMapper.readValue(
                response.body(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, NBRBExchangeRateDto.class)
        );

        Set<String> supportedCurrencies = EnumSet.allOf(CurrencyCodeEnum.class).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        List<ExchangeRateDto> rates = nbrbExchangeRateDtos.stream()
                .filter(rate -> supportedCurrencies.contains(rate.getFromCurrency()))
                .map(exchangeRateMapper::fromNBRB)
                .collect(Collectors.toList());

        log.info("Загружено {} базовых курсов из НБРБ за {}", rates.size(), date);
        return rates;
    }

    private HttpResponse<String> sendWithRetry(HttpRequest request)
            throws IOException, InterruptedException, HttpNBRBLoaderException {
        IOException lastIoException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response;
                }

                log.warn("НБРБ вернул HTTP {} (попытка {}/{})", response.statusCode(), attempt, maxAttempts);
            } catch (IOException exception) {
                lastIoException = exception;
                log.warn("Ошибка запроса к НБРБ (попытка {}/{}): {}", attempt, maxAttempts, exception.getMessage());
            }

            if (attempt < maxAttempts) {
                Thread.sleep(250L * attempt);
            }
        }

        if (lastIoException != null) {
            throw new HttpNBRBLoaderException("Не удалось загрузить курсы НБРБ", lastIoException);
        }
        throw new HttpNBRBLoaderException("НБРБ не вернул успешный ответ после " + maxAttempts + " попыток");
    }

}
