package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateAdminService {

    List<ExchangeRateDto> getAllExchangeRates(LocalDate date);

    ExchangeRateDto getExchangeRateById(Long id) throws DataNotFoundException;

    ExchangeRateDto createExchangeRate(ExchangeRateDto exchangeRateDto);

    ExchangeRateDto updateExchangeRate(Long id, ExchangeRateDto exchangeRateDto) throws DataNotFoundException;

    void deleteExchangeRate(Long id) throws DataNotFoundException;
}
