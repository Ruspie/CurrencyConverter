package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.util.List;

public interface ExchangeRatesLoaderService {

    List<ExchangeRateDto> loadRates() throws IOException, InterruptedException, HttpNBRBLoaderException;

}
