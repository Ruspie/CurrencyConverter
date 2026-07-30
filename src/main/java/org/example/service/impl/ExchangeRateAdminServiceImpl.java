package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.exception.DataNotFoundException;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.example.service.ExchangeRateAdminService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateAdminServiceImpl implements ExchangeRateAdminService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ExchangeRateDto> getAllExchangeRates(LocalDate date) {
        List<ExchangeRateEntity> entities = date != null
                ? exchangeRateRepository.findAllByRateDateOrderByFromCurrencyAscToCurrencyAsc(date)
                : exchangeRateRepository.findAll();

        return entities.stream()
                .map(entity -> modelMapper.map(entity, ExchangeRateDto.class))
                .toList();
    }

    @Override
    public ExchangeRateDto getExchangeRateById(Long id) throws DataNotFoundException {
        return modelMapper.map(findEntity(id), ExchangeRateDto.class);
    }

    @Override
    @Transactional
    public ExchangeRateDto createExchangeRate(ExchangeRateDto exchangeRateDto) {
        validateRate(exchangeRateDto);

        exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyAndRateDate(
                exchangeRateDto.getFromCurrency().name(),
                exchangeRateDto.getToCurrency().name(),
                exchangeRateDto.getRateDate()
        ).ifPresent(existing -> {
            throw new IllegalArgumentException(
                    "Курс уже существует для пары %s/%s на дату %s"
                            .formatted(
                                    exchangeRateDto.getFromCurrency(),
                                    exchangeRateDto.getToCurrency(),
                                    exchangeRateDto.getRateDate()
                            )
            );
        });

        ExchangeRateEntity entity = modelMapper.map(exchangeRateDto, ExchangeRateEntity.class);
        entity.setId(null);
        ExchangeRateEntity saved = exchangeRateRepository.save(entity);
        return modelMapper.map(saved, ExchangeRateDto.class);
    }

    @Override
    @Transactional
    public ExchangeRateDto updateExchangeRate(Long id, ExchangeRateDto exchangeRateDto) throws DataNotFoundException {
        validateRate(exchangeRateDto);
        ExchangeRateEntity entity = findEntity(id);

        entity.setFromCurrency(exchangeRateDto.getFromCurrency().name());
        entity.setToCurrency(exchangeRateDto.getToCurrency().name());
        entity.setRate(exchangeRateDto.getExchangeRate());
        entity.setScale(exchangeRateDto.getScale());
        entity.setRateDate(exchangeRateDto.getRateDate());

        ExchangeRateEntity saved = exchangeRateRepository.save(entity);
        return modelMapper.map(saved, ExchangeRateDto.class);
    }

    @Override
    @Transactional
    public void deleteExchangeRate(Long id) throws DataNotFoundException {
        ExchangeRateEntity entity = findEntity(id);
        exchangeRateRepository.delete(entity);
    }

    private ExchangeRateEntity findEntity(Long id) throws DataNotFoundException {
        return exchangeRateRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        "Курс с id=" + id + " не найден",
                        null,
                        null
                ));
    }

    private void validateRate(ExchangeRateDto dto) {
        if (dto.getFromCurrency() == null || dto.getToCurrency() == null) {
            throw new IllegalArgumentException("Валюты from/to обязательны");
        }
        if (dto.getFromCurrency() == dto.getToCurrency()) {
            throw new IllegalArgumentException("Валюты from и to должны отличаться");
        }
        if (dto.getExchangeRate() == null || dto.getExchangeRate().signum() <= 0) {
            throw new IllegalArgumentException("Курс должен быть положительным числом");
        }
        if (dto.getScale() == null || dto.getScale().signum() <= 0) {
            throw new IllegalArgumentException("Scale должен быть положительным числом");
        }
        if (dto.getRateDate() == null) {
            throw new IllegalArgumentException("Дата курса обязательна");
        }
    }
}
