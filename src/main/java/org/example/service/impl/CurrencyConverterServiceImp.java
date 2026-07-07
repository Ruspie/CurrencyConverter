package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.exception.DataNotFoundException;
import org.example.exception.HttpNBRBLoaderException;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.example.service.CurrencyConverterService;
import org.example.service.ExchangeRatesLoaderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterServiceImp implements CurrencyConverterService {

    private final ExchangeRatesLoaderService exchangeRatesFileLoader;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    public List<ExchangeRateDto> exchangeRates = new ArrayList<>();

    @Override
    public void loadExchangeRates() throws IOException, HttpNBRBLoaderException, InterruptedException {
        List<ExchangeRateDto> loadedRates = exchangeRatesFileLoader.loadRates();
        List<ExchangeRateEntity> allEntities = exchangeRateRepository.findAll();

        log.debug("Loaded rates count: " + loadedRates.size());
        log.debug("Entities in DB: " + allEntities.size());
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRates() {
        return exchangeRateRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }

    private void generateAnotherExchangeRates(ExchangeRateDto exchangeRateBYNUSD, ExchangeRateDto exchangeRateBYNEUR, ExchangeRateDto exchangeRateBYNRUB) {
        ExchangeRateDto exchangeRateUSDBYN = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateEURBYN = new ExchangeRateDto(CurrencyCodeEnum.EUR, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateRUBBYN = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.BYN, BigDecimal.ONE.divide(exchangeRateBYNRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.valueOf(1.0 / 100.0));

        addExchangeRate(exchangeRateUSDBYN);
        addExchangeRate(exchangeRateEURBYN);
        addExchangeRate(exchangeRateRUBBYN);

        ExchangeRateDto exchangeRateUSDEUR = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, exchangeRateUSDBYN.getExchangeRate().divide(exchangeRateEURBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateEURUSD = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateUSDEUR.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRateDto exchangeRateRUBUSD = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.USD, exchangeRateRUBBYN.getExchangeRate().divide(exchangeRateUSDBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateUSDRUB = new ExchangeRateDto(CurrencyCodeEnum.USD, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateRUBUSD.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        ExchangeRateDto exchangeRateEURRUB = new ExchangeRateDto(CurrencyCodeEnum.EUR, CurrencyCodeEnum.RUB, exchangeRateEURBYN.getExchangeRate().divide(exchangeRateRUBBYN.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);
        ExchangeRateDto exchangeRateRUBEUR = new ExchangeRateDto(CurrencyCodeEnum.RUB, CurrencyCodeEnum.EUR, BigDecimal.ONE.divide(exchangeRateEURRUB.getExchangeRate(), 10, RoundingMode.HALF_UP), BigDecimal.ONE);

        addExchangeRate(exchangeRateUSDEUR);
        addExchangeRate(exchangeRateEURUSD);
        addExchangeRate(exchangeRateRUBUSD);
        addExchangeRate(exchangeRateUSDRUB);
        addExchangeRate(exchangeRateEURRUB);
        addExchangeRate(exchangeRateRUBEUR);
    }

    @Override
    public boolean addExchangeRate(ExchangeRateDto exchangeRateDto) {
        ExchangeRateEntity entity = modelMapper.map(exchangeRateDto, ExchangeRateEntity.class);
        exchangeRateRepository.save(entity);
        return true;
    }

    @Override
    public SumDto exchangeSum(SumDto sumDto, CurrencyCodeEnum destinationCurrency) throws DataNotFoundException {
        ExchangeRateEntity currentExchangeRate = exchangeRateRepository
                .findByCurrencyPair(sumDto.getCurrency().name(), destinationCurrency.name())
                .orElseThrow(() -> new DataNotFoundException(
                        "Не найден курс конверсии", sumDto.getCurrency(), destinationCurrency));

        BigDecimal result = sumDto.getSum()
                .multiply(currentExchangeRate.getRate())
                .multiply(currentExchangeRate.getScale())
                .setScale(2, RoundingMode.HALF_UP);

        SumDto sumDtoResult = new SumDto(result, destinationCurrency);
        sumDtoResult.print(sumDto);
        return sumDtoResult;
    }

    @Override
    public void printAllCurrencyExchangeRates() {
        List<ExchangeRateEntity> allRates = exchangeRateRepository.findAll();
        for (ExchangeRateEntity rate : allRates) {
            log.debug(rate.getFromCurrency() + " => " + rate.getToCurrency() +
                    " - " + rate.getRate() + " (1:" + rate.getScale() + ")");
        }
    }

}
