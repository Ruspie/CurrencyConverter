package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.exception.DataNotFoundException;
import org.example.exception.HttpNBRBLoaderException;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.example.service.CurrencyConverterService;
import org.example.service.ExchangeRatesLoaderService;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterServiceImp implements CurrencyConverterService {

    private final ExchangeRatesLoaderService exchangeRatesFileLoader;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    public List<ExchangeRateDto> exchangeRates = new ArrayList<>();

    public void loadExchangeRates(LocalDate date) throws IOException, HttpNBRBLoaderException, InterruptedException {
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRatesFileLoader.loadRates(date).stream()
                .map(exchangeRateDto -> modelMapper.map(exchangeRateDto, ExchangeRateEntity.class))
                .toList();

        Set<String> storedExchangeRateKeys = exchangeRateRepository.findAllByRateDate(date).stream()
                .map(this::getRateKey)
                .collect(Collectors.toSet());

        exchangeRateEntities = exchangeRateEntities.stream()
                .peek(exchangeRateEntity -> exchangeRateEntity.setRateDate(date))
                .filter(exchangeRateEntity -> !storedExchangeRateKeys.contains(getRateKey(exchangeRateEntity)))
                .collect(Collectors.toList());

        exchangeRateRepository.saveAll(exchangeRateEntities);

        List<ExchangeRateDto> exchangeRateDtos = exchangeRateRepository.findAll().stream()
                .map(exchangeRateEntity -> modelMapper.map(exchangeRateEntity, ExchangeRateDto.class))
                .toList();
        log.debug(exchangeRateDtos.toString());
    }

    private String getRateKey(ExchangeRateEntity entity) {
        return entity.getToCurrency() + ";" + entity.getFromCurrency() + ";" + entity.getRateDate();
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRates(LocalDate date) {
        return exchangeRateRepository.findAllByRateDateOrderByFromCurrencyAscToCurrencyAsc(date)
                .stream()
                .map(exchangeRateEntity ->
                        modelMapper.map(exchangeRateEntity, ExchangeRateDto.class)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDate> getAvailableDates() {
        return exchangeRateRepository.findDistinctRateDates();
    }

    public boolean addExchangeRate(ExchangeRateDto exchangeRate) {
        for (ExchangeRateDto rate : exchangeRates) {
            if (rate != null) {
                if (rate.getFromCurrency().equals(exchangeRate.getFromCurrency())
                        && rate.getToCurrency().equals(exchangeRate.getToCurrency())) {
                    rate.setExchangeRate(exchangeRate.getExchangeRate());
                    return true;
                }
            }
        }

        exchangeRates.add(exchangeRate);
        return true;
    }

    private ExchangeRateDto getCurrentExchangeRate(CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency) {
        for (ExchangeRateDto rate : exchangeRates) {
            if (rate != null) {

                if (rate.getFromCurrency().equals(fromCurrency)
                        && rate.getToCurrency().equals(toCurrency)) {
                    return rate;
                }
            }
        }

        return null;
    }

    @Override
    public SumDto exchangeSum(SumDto sumDto, CurrencyCodeEnum destinationCurrency, LocalDate date) throws DataNotFoundException {
        ExchangeRateEntity currentExchangeRate = exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyAndRateDate(
                sumDto.getCurrency().name(), destinationCurrency.name(), date
        ).orElseThrow(() -> new DataNotFoundException("Не найден курс конверсии", sumDto.getCurrency(), destinationCurrency));

        modelMapper.map(currentExchangeRate, ExchangeRateEntity.class);

        /// TODO Сделать кросс конверсию

        BigDecimal result = sumDto.getSum()
                .multiply(currentExchangeRate.getRate())
                .multiply(currentExchangeRate.getScale())
                .setScale(2, RoundingMode.HALF_UP);

        SumDto sumDtoResult = new SumDto(
                result, destinationCurrency
        );
        sumDtoResult.print(sumDto);
        return sumDtoResult;
    }

    @Override
    public void printAllCurrencyExchangeRates() {
        for (ExchangeRateDto exchangeRate : exchangeRates) {
            if (exchangeRate != null)
                log.debug(exchangeRate.toString());
        }
    }

}
