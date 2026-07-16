package org.example.repository.entity.mapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.dto.ExchangeRateDto;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.external.NBRBExchangeRateDto;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExchangeRateMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    @SuppressWarnings("null")
    public void setupMapperConfig() {
        Converter<String, CurrencyCodeEnum> toCurrencyCode =
                new AbstractConverter<>() {
                    @Override
                    protected CurrencyCodeEnum convert(String source) {
                        return CurrencyCodeEnum.valueOf(source);
                    }
                };
        Converter<CurrencyCodeEnum, String> toCurrencyName =
                new AbstractConverter<>() {
                    @Override
                    protected String convert(CurrencyCodeEnum source) {
                        return source.name();
                    }
                };
        Converter<String, LocalDate> toLocalDate =
                new AbstractConverter<>() {
                    @Override
                    protected LocalDate convert(String source) {
                        return LocalDate.parse(source.substring(0, 10));
                    }
                };

        modelMapper.typeMap(NBRBExchangeRateDto.class, ExchangeRateDto.class)
                .addMappings(mapping -> {
                    mapping.using(toCurrencyCode)
                            .map(NBRBExchangeRateDto::getFromCurrency, ExchangeRateDto::setFromCurrency);
                    mapping.using(toCurrencyCode)
                            .map(source -> CurrencyCodeEnum.BYN.name(), ExchangeRateDto::setToCurrency);
                    mapping.map(NBRBExchangeRateDto::getExchangeRate, ExchangeRateDto::setExchangeRate);
                    mapping.map(NBRBExchangeRateDto::getScale, ExchangeRateDto::setScale);
                    mapping.using(toLocalDate)
                            .map(NBRBExchangeRateDto::getDate, ExchangeRateDto::setRateDate);
                });

        modelMapper.typeMap(ExchangeRateEntity.class, ExchangeRateDto.class)
                .addMappings(mapping -> {
                    mapping.using(toCurrencyCode)
                            .map(ExchangeRateEntity::getFromCurrency, ExchangeRateDto::setFromCurrency);
                    mapping.using(toCurrencyCode)
                            .map(ExchangeRateEntity::getToCurrency, ExchangeRateDto::setToCurrency);
                    mapping.map(ExchangeRateEntity::getRate, ExchangeRateDto::setExchangeRate);
                    mapping.map(ExchangeRateEntity::getScale, ExchangeRateDto::setScale);
                    mapping.map(ExchangeRateEntity::getRateDate, ExchangeRateDto::setRateDate);
                });

        modelMapper.typeMap(ExchangeRateDto.class, ExchangeRateEntity.class)
                .addMappings(mapping -> {
                    mapping.using(toCurrencyName)
                            .map(ExchangeRateDto::getFromCurrency, ExchangeRateEntity::setFromCurrency);
                    mapping.using(toCurrencyName)
                            .map(ExchangeRateDto::getToCurrency, ExchangeRateEntity::setToCurrency);
                    mapping.map(ExchangeRateDto::getExchangeRate, ExchangeRateEntity::setRate);
                    mapping.map(ExchangeRateDto::getScale, ExchangeRateEntity::setScale);
                    mapping.map(ExchangeRateDto::getRateDate, ExchangeRateEntity::setRateDate);
                });
    }

    public ExchangeRateDto fromNBRB(NBRBExchangeRateDto source) {
        return modelMapper.map(source, ExchangeRateDto.class);
    }

    public ExchangeRateDto toDto(ExchangeRateEntity source) {
        return modelMapper.map(source, ExchangeRateDto.class);
    }

    public ExchangeRateEntity toEntity(ExchangeRateDto source) {
        return modelMapper.map(source, ExchangeRateEntity.class);
    }
}
