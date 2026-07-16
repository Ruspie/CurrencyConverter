package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRateDto;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.ExchangeRatesLoaderService;
import org.modelmapper.ModelMapper;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "http")
@Slf4j
public class HttpNBRBExchangeRatesLoaderService implements ExchangeRatesLoaderService {

    public static final String BASE_RATES_URL = "https://api.nbrb.by/exrates/rates";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    @Value("${loading.timeout}")
    private Duration timeout;

    public HttpNBRBExchangeRatesLoaderService(ModelMapper modelMapper) {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ExchangeRateDto> loadRates(LocalDate date) throws IOException, InterruptedException, HttpNBRBLoaderException {

        log.debug("Я http");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_RATES_URL + "?periodicity=0&ondate=" + date))
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new HttpNBRBLoaderException("Запрос НБРБ вернул ошибку");

        List<NBRBExchangeRateDto> nbrbExchangeRateDtos = objectMapper.readValue(
                response.body(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, NBRBExchangeRateDto.class)
        );

        return nbrbExchangeRateDtos.stream()
                .filter(nbrbExchangeRateDto -> Arrays.stream(CurrencyCodeEnum.values())
                        .map(Enum::name)
                        .toList()
                        .contains(nbrbExchangeRateDto.getFromCurrency())
                )
                .map(nbrbExchangeRateDto -> modelMapper.map(nbrbExchangeRateDto, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

}
