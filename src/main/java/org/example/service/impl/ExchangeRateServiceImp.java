package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImp {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    public List<ExchangeRateDto> getAllExchangeRates() {
        return exchangeRateRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveExchangeRate(ExchangeRateDto exchangeRate) {
        ExchangeRateEntity entity = modelMapper.map(exchangeRate, ExchangeRateEntity.class);
        exchangeRateRepository.save(entity);
    }

    @Transactional
    public void deleteExchangeRate(ExchangeRateDto exchangeRate) {
        ExchangeRateEntity entity = modelMapper.map(exchangeRate, ExchangeRateEntity.class);
        exchangeRateRepository.delete(entity);
    }

    public List<ExchangeRateDto> findWithFilters(String from, String to, Boolean scaleGtOne) {
        return exchangeRateRepository.findWithFilters(from, to, scaleGtOne)
                .stream()
                .map(entity -> modelMapper.map(entity, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

}