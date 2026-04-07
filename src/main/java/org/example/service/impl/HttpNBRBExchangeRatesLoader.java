package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ModelMapperConfig;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRate;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.ExchangeRatesLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpNBRBExchangeRatesLoader implements ExchangeRatesLoader {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpNBRBExchangeRatesLoader() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public List<ExchangeRateDto> loadRates() throws IOException, InterruptedException, HttpNBRBLoaderException {

        System.out.println("Я http");

        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nbrb.by/exrates/rates?periodicity=0"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new HttpNBRBLoaderException("Запрос НБРБ вернул ошибку");

        List<NBRBExchangeRate> nbrbExchangeRates = objectMapper.readValue(
                response.body(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, NBRBExchangeRate.class)
        );

        return nbrbExchangeRates.stream()
                .filter(nbrbExchangeRate -> Arrays.stream(CurrencyCodeEnum.values())
                        .map(Enum::name)
                        .toList()
                        .contains(nbrbExchangeRate.getToCurrency())
                )
                .map(nbrbExchangeRate -> ModelMapperConfig.getInstance().map(nbrbExchangeRate, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

}
