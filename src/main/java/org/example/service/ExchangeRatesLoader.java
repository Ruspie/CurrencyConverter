package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.util.List;

public interface ExchangeRatesLoader {

    List<ExchangeRateDto> loadRates() throws IOException, InterruptedException, HttpNBRBLoaderException;

}
