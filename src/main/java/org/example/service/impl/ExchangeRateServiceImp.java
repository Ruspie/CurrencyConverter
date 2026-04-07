package org.example.service.impl;

import org.example.config.ModelMapperConfig;
import org.example.dto.ExchangeRateDto;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateServiceImp {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImp(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRateRepository.findAll();

        return exchangeRateEntities.stream()
                .map(exchangeRateEntity -> ModelMapperConfig.getInstance().map(exchangeRateEntity, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

    public void saveExchangeRate(ExchangeRateDto exchangeRateDto) {
        ExchangeRateEntity exchangeRateEntity = ModelMapperConfig.getInstance().map(exchangeRateDto, ExchangeRateEntity.class);

        exchangeRateRepository.insert(exchangeRateEntity);
    }

    public void deleteExchangeRate(ExchangeRateDto exchangeRateDto) {
        ExchangeRateEntity exchangeRateEntity = ModelMapperConfig.getInstance().map(exchangeRateDto, ExchangeRateEntity.class);

        exchangeRateRepository.delete(exchangeRateEntity);
    }

}
