package org.example.service.impl;

import org.example.config.ModelMapperConfig;
import org.example.dto.ExchangeRate;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateServiceImp {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImp(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRateRepository.findAll();

        return exchangeRateEntities.stream()
                .map(exchangeRateEntity -> ModelMapperConfig.getInstance().map(exchangeRateEntity, ExchangeRate.class))
                .collect(Collectors.toList());
    }

    public void saveExchangeRate(ExchangeRate exchangeRate) {
        ExchangeRateEntity exchangeRateEntity = ModelMapperConfig.getInstance().map(exchangeRate, ExchangeRateEntity.class);

        exchangeRateRepository.insert(exchangeRateEntity);
    }

    public void deleteExchangeRate(ExchangeRate exchangeRate) {
        ExchangeRateEntity exchangeRateEntity = ModelMapperConfig.getInstance().map(exchangeRate, ExchangeRateEntity.class);

        exchangeRateRepository.delete(exchangeRateEntity);
    }

}
