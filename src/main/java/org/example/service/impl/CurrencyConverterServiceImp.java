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
import org.example.repository.entity.mapper.ExchangeRateMapper;
import org.example.service.CurrencyConverterService;
import org.example.service.ExchangeRatesLoaderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterServiceImp implements CurrencyConverterService {

    private static final int RATE_SCALE = 12;

    private final ExchangeRatesLoaderService exchangeRatesLoader;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;

    @Transactional
    @SuppressWarnings("null")
    public void loadExchangeRates(LocalDate rateDate)
            throws IOException, HttpNBRBLoaderException, InterruptedException {
        List<ExchangeRateEntity> directRates = exchangeRatesLoader.loadRates(rateDate).stream()
                .filter(rate -> rate.getToCurrency() == CurrencyCodeEnum.BYN)
                .map(exchangeRateMapper::toEntity)
                .toList();

        if (directRates.isEmpty()) {
            throw new IllegalArgumentException("Источник не вернул прямые курсы к BYN");
        }

        Set<LocalDate> rateDates = directRates.stream()
                .map(ExchangeRateEntity::getRateDate)
                .collect(Collectors.toSet());
        Set<String> existingRates = exchangeRateRepository.findAllByRateDateIn(rateDates).stream()
                .map(this::rateKey)
                .collect(Collectors.toCollection(HashSet::new));
        List<ExchangeRateEntity> newRates = directRates.stream()
                .filter(rate -> !existingRates.contains(rateKey(rate)))
                .toList();

        exchangeRateRepository.saveAll(newRates);
        log.info("Сохранено {} прямых курсов валют к BYN за даты {}", newRates.size(), rateDates);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates(LocalDate rateDate) {
        return exchangeRateRepository.findAllByRateDateOrderByFromCurrencyAscToCurrencyAsc(rateDate).stream()
                .map(exchangeRateMapper::toDto)
                .toList();
    }

    @Override
    public List<LocalDate> getAvailableRateDates() {
        return exchangeRateRepository.findDistinctRateDates();
    }

    @Override
    public SumDto exchangeSum(
            SumDto sumDto,
            CurrencyCodeEnum destinationCurrency,
            LocalDate rateDate
    ) throws DataNotFoundException {
        if (sumDto.getCurrency() == destinationCurrency) {
            return new SumDto(sumDto.getSum().setScale(2, RoundingMode.HALF_UP), destinationCurrency);
        }

        BigDecimal sourceBynPerUnit = getBynPerUnit(sumDto.getCurrency(), rateDate);
        BigDecimal destinationBynPerUnit = getBynPerUnit(destinationCurrency, rateDate);
        BigDecimal result = sumDto.getSum()
                .multiply(sourceBynPerUnit)
                .divide(destinationBynPerUnit, RATE_SCALE, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        return new SumDto(result, destinationCurrency);
    }

    private String rateKey(ExchangeRateEntity rate) {
        return rate.getFromCurrency() + "|" + rate.getToCurrency() + "|" + rate.getRateDate();
    }

    private BigDecimal getBynPerUnit(CurrencyCodeEnum currency, LocalDate rateDate)
            throws DataNotFoundException {
        if (currency == CurrencyCodeEnum.BYN) {
            return BigDecimal.ONE;
        }

        ExchangeRateEntity rate = exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(
                currency.name(), CurrencyCodeEnum.BYN.name(), rateDate
        ).orElseThrow(() -> new DataNotFoundException(
                "Не найден прямой курс к BYN за " + rateDate,
                currency,
                CurrencyCodeEnum.BYN
        ));

        return rate.getRate().divide(rate.getScale(), RATE_SCALE, RoundingMode.HALF_UP);
    }
}
