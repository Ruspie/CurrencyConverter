package org.example.mapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRateDto;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeRateMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapperConfig() {
        Converter<String, CurrencyCodeEnum> getCurrencyCodeEnumFromStringConverter = new AbstractConverter<>() {
            @Override
            protected CurrencyCodeEnum convert(String currencyCode) {
                return CurrencyCodeEnum.valueOf(currencyCode);
            }
        };

        Converter<CurrencyCodeEnum, String> getCurrencyCodeStringFromEnumConverter = new AbstractConverter<>() {
            @Override
            protected String convert(CurrencyCodeEnum currencyCodeEnum) {
                return currencyCodeEnum.name();
            }
        };

        modelMapper.typeMap(NBRBExchangeRateDto.class, ExchangeRateDto.class)
                .addMappings(mapper -> {
                    mapper.using(getCurrencyCodeEnumFromStringConverter).map(NBRBExchangeRateDto::getFromCurrency, ExchangeRateDto::setFromCurrency);
                    mapper.using(getCurrencyCodeEnumFromStringConverter).map(nbrbExchangeRateDto -> "BYN", ExchangeRateDto::setToCurrency);
                    mapper.map(NBRBExchangeRateDto::getScale, ExchangeRateDto::setScale);
                    mapper.map(NBRBExchangeRateDto::getExchangeRate, ExchangeRateDto::setExchangeRate);
                });

        modelMapper.typeMap(ExchangeRateEntity.class, ExchangeRateDto.class)
                .addMappings(mapper -> {
                    mapper.map(ExchangeRateEntity::getId, ExchangeRateDto::setId);
                    mapper.using(getCurrencyCodeEnumFromStringConverter).map(ExchangeRateEntity::getFromCurrency, ExchangeRateDto::setFromCurrency);
                    mapper.using(getCurrencyCodeEnumFromStringConverter).map(ExchangeRateEntity::getToCurrency, ExchangeRateDto::setToCurrency);
                    mapper.map(ExchangeRateEntity::getScale, ExchangeRateDto::setScale);
                    mapper.map(ExchangeRateEntity::getRate, ExchangeRateDto::setExchangeRate);
                    mapper.map(ExchangeRateEntity::getRateDate, ExchangeRateDto::setRateDate);
                });

        modelMapper.typeMap(ExchangeRateDto.class, ExchangeRateEntity.class)
                .addMappings(mapper -> {
                    mapper.map(ExchangeRateDto::getId, ExchangeRateEntity::setId);
                    mapper.using(getCurrencyCodeStringFromEnumConverter).map(ExchangeRateDto::getFromCurrency, ExchangeRateEntity::setFromCurrency);
                    mapper.using(getCurrencyCodeStringFromEnumConverter).map(ExchangeRateDto::getToCurrency, ExchangeRateEntity::setToCurrency);
                    mapper.map(ExchangeRateDto::getScale, ExchangeRateEntity::setScale);
                    mapper.map(ExchangeRateDto::getExchangeRate, ExchangeRateEntity::setRate);
                    mapper.map(ExchangeRateDto::getRateDate, ExchangeRateEntity::setRateDate);
                });

        /// TODO Добавить новый маппер
    }

}
