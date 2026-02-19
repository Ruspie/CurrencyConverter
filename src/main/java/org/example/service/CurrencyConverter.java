package org.example.service;

import org.example.dto.ExchangeRate;
import org.example.dto.Sum;
import org.example.exception.DataNotFoundException;
import org.example.dto.CurrencyCodeEnum;

public interface CurrencyConverter {

    void printAllCurrencyExchangeRates();

    Sum exchangeSum(Sum sum, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException;

    boolean addExchangeRate(ExchangeRate exchangeRate);

}
