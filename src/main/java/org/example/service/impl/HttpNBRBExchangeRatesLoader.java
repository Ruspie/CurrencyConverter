package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ModelMapperConfig;
import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.external.NBRBExchangeRate;
import org.example.exception.HttpNBRBLoaderException;
import org.example.service.ExchangeRatesLoader;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "loading.mode", havingValue = "http")
public class HttpNBRBExchangeRatesLoader implements ExchangeRatesLoader {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public HttpNBRBExchangeRatesLoader(ModelMapper modelMapper) {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ExchangeRate> loadRates() throws IOException, InterruptedException, HttpNBRBLoaderException {

        System.out.println("Я http");

        List<ExchangeRate> exchangeRates = new ArrayList<>();

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
                .map(nbrbExchangeRate -> modelMapper.map(nbrbExchangeRate, ExchangeRate.class))
                .collect(Collectors.toList());
    }

}
