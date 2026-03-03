package org.example.service;

import org.example.dto.ExchangeRate;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.util.List;

public interface ExchangeRatesLoader {

    List<ExchangeRate> loadRates() throws IOException, InterruptedException, HttpNBRBLoaderException;

}
