package org.example.service;

import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.dto.CurrencyCodeEnum;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;

public interface CurrencyConverter {

    void printAllCurrencyExchangeRates();

    Sum exchangeSum(Sum sum, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException;

    boolean addExchangeRate(ExchangeRate exchangeRate);

    void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException;

}
