package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.exception.DataNotFoundException;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface CurrencyConverterService {

    SumDto exchangeSum(SumDto sumDto, CurrencyCodeEnum destinationCurrency, LocalDate rateDate)
            throws DataNotFoundException;

    void loadExchangeRates(LocalDate rateDate)
            throws IOException, HttpNBRBLoaderException, InterruptedException;

    List<ExchangeRateDto> getExchangeRates(LocalDate rateDate);

    List<LocalDate> getAvailableRateDates();
}
