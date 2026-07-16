package org.example.service.impl;

import org.example.dto.ExchangeRateDto;
import org.example.dto.SumDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.example.repository.entity.mapper.ExchangeRateMapper;
import org.example.service.ExchangeRatesLoaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterServiceImpTest {

    @Mock
    private ExchangeRatesLoaderService exchangeRatesLoader;
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @InjectMocks
    private CurrencyConverterServiceImp service;

    @Test
    void savesOnlyRatesMissingFromHistory() throws Exception {
        LocalDate rateDate = LocalDate.of(2026, 7, 15);
        ExchangeRateDto source = new ExchangeRateDto(
                CurrencyCodeEnum.USD, CurrencyCodeEnum.BYN,
                new BigDecimal("3.2"), BigDecimal.ONE, rateDate
        );
        when(exchangeRatesLoader.loadRates(rateDate)).thenReturn(List.of(source));
        when(exchangeRateMapper.toEntity(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    ExchangeRateDto dto = invocation.getArgument(0);
                    return ExchangeRateEntity.builder()
                            .fromCurrency(dto.getFromCurrency().name())
                            .toCurrency(dto.getToCurrency().name())
                            .rateDate(dto.getRateDate())
                            .build();
                });
        when(exchangeRateRepository.findAllByRateDateIn(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        service.loadExchangeRates(rateDate);

        verify(exchangeRateRepository).saveAll(org.mockito.ArgumentMatchers.argThat(items ->
                ((List<?>) items).size() == 1));
    }

    @Test
    void convertsUsdToBynUsingDirectNBRBRate() throws Exception {
        ExchangeRateEntity rate = ExchangeRateEntity.builder()
                .fromCurrency("USD")
                .toCurrency("BYN")
                .rate(new BigDecimal("3.2"))
                .scale(BigDecimal.ONE)
                .build();
        LocalDate rateDate = LocalDate.of(2026, 7, 15);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate("USD", "BYN", rateDate))
                .thenReturn(Optional.of(rate));

        SumDto result = service.exchangeSum(
                new SumDto(new BigDecimal("10"), CurrencyCodeEnum.USD),
                CurrencyCodeEnum.BYN,
                rateDate
        );

        assertThat(result.getSum()).isEqualByComparingTo("32.00");
    }

    @Test
    void convertsBynToUsdUsingInverseDirectRate() throws Exception {
        ExchangeRateEntity rate = ExchangeRateEntity.builder()
                .fromCurrency("USD")
                .toCurrency("BYN")
                .rate(new BigDecimal("3.2"))
                .scale(BigDecimal.ONE)
                .build();
        LocalDate rateDate = LocalDate.of(2026, 7, 15);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate("USD", "BYN", rateDate))
                .thenReturn(Optional.of(rate));

        SumDto result = service.exchangeSum(
                new SumDto(new BigDecimal("32"), CurrencyCodeEnum.BYN),
                CurrencyCodeEnum.USD,
                rateDate
        );

        assertThat(result.getSum()).isEqualByComparingTo("10.00");
    }
}
