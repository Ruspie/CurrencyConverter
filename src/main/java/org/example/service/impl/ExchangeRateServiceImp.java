package org.example.service.impl;

import org.example.dto.ExchangeRateDto;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImp {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    public ExchangeRateServiceImp(ExchangeRateRepository exchangeRateRepository, ModelMapper modelMapper) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.modelMapper = modelMapper;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRateRepository.findAll();

        return exchangeRateEntities.stream()
                .map(exchangeRateEntity -> modelMapper.map(exchangeRateEntity, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

    public void saveExchangeRate(ExchangeRateDto exchangeRate) {
        ExchangeRateEntity exchangeRateEntity = modelMapper.map(exchangeRate, ExchangeRateEntity.class);

        exchangeRateRepository.save(exchangeRateEntity);
    }

    public void deleteExchangeRate(ExchangeRateDto exchangeRate) {
        ExchangeRateEntity exchangeRateEntity = modelMapper.map(exchangeRate, ExchangeRateEntity.class);

        exchangeRateRepository.delete(exchangeRateEntity);
    }

}
