package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.exception.DataNotFoundException;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.util.List;

public interface CurrencyConverterService {

    void printAllCurrencyExchangeRates();

    SumDto exchangeSum(SumDto sumDto, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException;

    boolean addExchangeRate(ExchangeRateDto exchangeRateDto);

    void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException;

    List<ExchangeRateDto> getAllExchangeRates();
}
