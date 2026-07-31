package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.example.service.AdminExchangeRateService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminExchangeRateServiceImpl implements AdminExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    @Override
    public ExchangeRateDto getExchangeRateById(Long rateId) throws DataNotFoundException {

        return modelMapper.map(
                exchangeRateRepository.findById(rateId).orElseThrow(() -> new DataNotFoundException(
                        "Курс с id=" + rateId + " не найден", null, null
                )), ExchangeRateDto.class);

    }

    @Override
    @Transactional
    public ExchangeRateDto createExchangeRate(ExchangeRateDto exchangeRateDto) {

        exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyAndRateDate(
                exchangeRateDto.getFromCurrency().name(),
                exchangeRateDto.getToCurrency().name(),
                exchangeRateDto.getRateDate()
        ).ifPresent(exchangeRateEntity -> {
            throw new IllegalArgumentException("Курс уже существует для пары %s/%s на дату %s".formatted(
                    exchangeRateDto.getFromCurrency().name(),
                    exchangeRateDto.getToCurrency().name(),
                    exchangeRateDto.getRateDate()
            ));
        });

        return modelMapper.map(
                exchangeRateRepository.save(
                        modelMapper.map(exchangeRateDto, ExchangeRateEntity.class)
                ), ExchangeRateDto.class);

    }

    @Override
    @Transactional
    public ExchangeRateDto updateExchangeRate(Long rateId, ExchangeRateDto exchangeRateDto) throws DataNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateRepository.findById(rateId).orElseThrow(() ->
                new DataNotFoundException("Курс с id=" + rateId + " не найден", exchangeRateDto.getFromCurrency(), exchangeRateDto.getToCurrency())
        );

        exchangeRateEntity.setFromCurrency(exchangeRateDto.getFromCurrency().name());
        exchangeRateEntity.setToCurrency(exchangeRateDto.getToCurrency().name());
        exchangeRateEntity.setRate(exchangeRateDto.getExchangeRate());
        exchangeRateEntity.setRateDate(exchangeRateDto.getRateDate());
        exchangeRateEntity.setScale(exchangeRateDto.getScale());

        return modelMapper.map(exchangeRateRepository.save(exchangeRateEntity), ExchangeRateDto.class);
    }

    @Override
    @Transactional
    public void deleteExchangeRate(Long rateId) throws DataNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateRepository.findById(rateId).orElseThrow(() ->
                new DataNotFoundException("Курс с id=" + rateId + " не найден", null, null)
        );

        exchangeRateRepository.delete(exchangeRateEntity);
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRates(LocalDate date) {
        List<ExchangeRateEntity> rates = date != null ?
                exchangeRateRepository.findAllByRateDate(date) :
                exchangeRateRepository.findAll();

        return rates.stream()
                .map(entity -> modelMapper.map(entity, ExchangeRateDto.class))
                .toList();
    }

}
