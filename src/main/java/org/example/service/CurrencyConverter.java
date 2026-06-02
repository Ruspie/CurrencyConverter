package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.dto.CurrencyCodeEnum;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.util.List;

public interface CurrencyConverter {

    void printAllCurrencyExchangeRates();

    Sum exchangeSum(Sum sum, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException;

    boolean addExchangeRate(ExchangeRateDto exchangeRateDto);

    void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException;

    List<ExchangeRateDto> getAllExchangeRates();
}
