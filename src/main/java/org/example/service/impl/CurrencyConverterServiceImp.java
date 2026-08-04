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
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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
        if (sumDto.getCurrency().equals(destinationCurrency)) {
            return sumDto;
        }

        BigDecimal result = convertSum(sumDto.getSum(), sumDto.getCurrency(), destinationCurrency, date);

        SumDto sumDtoResult = new SumDto(
                result, destinationCurrency
        );
        sumDtoResult.print(sumDto);
        return sumDtoResult;
    }

    private BigDecimal convertSum(BigDecimal sum, CurrencyCodeEnum fromCurrency, CurrencyCodeEnum toCurrency, LocalDate date) throws DataNotFoundException {
        final CurrencyCodeEnum BASE_CURRENCY = CurrencyCodeEnum.BYN;

        Optional<ExchangeRateEntity> rate = findRate(fromCurrency, toCurrency, date);
        if (rate.isPresent()) {
            return applyDirectConversion(sum, rate.get());
        }

        rate = findRate(toCurrency, fromCurrency, date);
        if (rate.isPresent()) {
            return applyInverseConversion(sum, rate.get());
        }

        if (!BASE_CURRENCY.equals(fromCurrency) && !BASE_CURRENCY.equals(toCurrency)) {
            return applyCrossConversion(sum, fromCurrency, toCurrency, date, BASE_CURRENCY);
        }

        throw new DataNotFoundException("Не найден курс конверсии", fromCurrency, toCurrency);
    }

    private BigDecimal applyInverseConversion(BigDecimal sum, ExchangeRateEntity rate) {
        return sum
                .multiply(rate.getScale())
                .divide(rate.getRate(), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal applyDirectConversion(BigDecimal sum, ExchangeRateEntity rate) {
        return sum
                .multiply(rate.getRate())
                .divide(rate.getScale(), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private @Nullable BigDecimal applyCrossConversion(BigDecimal sum, CurrencyCodeEnum fromCurrency, CurrencyCodeEnum destinationCurrency, LocalDate date, CurrencyCodeEnum BASE_CURRENCY) throws DataNotFoundException {
        BigDecimal result = convertSum(sum, fromCurrency, BASE_CURRENCY, date);
        return convertSum(result, BASE_CURRENCY, destinationCurrency, date);
    }

    private Optional<ExchangeRateEntity> findRate(CurrencyCodeEnum from, CurrencyCodeEnum to, LocalDate date) {
        return exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyAndRateDate(from.name(), to.name(), date);
    }

    @Override
    public void printAllCurrencyExchangeRates() {
        for (ExchangeRateDto exchangeRate : exchangeRates) {
            if (exchangeRate != null)
                log.debug(exchangeRate.toString());
        }
    }

}
