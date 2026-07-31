package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface AdminExchangeRateService {

    ExchangeRateDto getExchangeRateById(Long rateId) throws DataNotFoundException;

    ExchangeRateDto createExchangeRate(@Valid ExchangeRateDto exchangeRateDto);

    ExchangeRateDto updateExchangeRate(@Valid Long rateId, ExchangeRateDto exchangeRateDto) throws DataNotFoundException;

    void deleteExchangeRate(Long rateId) throws DataNotFoundException;

    List<ExchangeRateDto> getAllExchangeRates(LocalDate date);
}
