package org.example.service;

import org.example.dto.ExchangeRateDto;
import org.example.exception.HttpNBRBLoaderException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ExchangeRatesLoaderService {

    List<ExchangeRateDto> loadRates(LocalDate date)
            throws IOException, InterruptedException, HttpNBRBLoaderException;

}
