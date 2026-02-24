package org.example.service;

import org.example.dto.ExchangeRate;

import java.util.List;

public interface ExchangeRatesLoader {

    List<ExchangeRate> loadRates();

}
