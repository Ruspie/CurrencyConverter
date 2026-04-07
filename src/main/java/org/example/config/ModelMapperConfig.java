package org.example.config;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRate;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class ModelMapperConfig {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        Converter<String, CurrencyCodeEnum> stringCurrencyCodeEnumConverter = mappingContext -> CurrencyCodeEnum.valueOf(mappingContext.getSource());
        Converter<CurrencyCodeEnum, String> currencyCodeEnumStringConverter = mappingContext -> mappingContext.getSource().name();

        modelMapper.typeMap(NBRBExchangeRate.class, ExchangeRateDto.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(nbrbExchangeRate -> "BYN", ExchangeRateDto::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(NBRBExchangeRate::getToCurrency, ExchangeRateDto::setToCurrency);
                    mapper.map(NBRBExchangeRate::getScale, ExchangeRateDto::setScale);
                    mapper.map(NBRBExchangeRate::getExchangeRate, ExchangeRateDto::setExchangeRate);
                });

        modelMapper.typeMap(ExchangeRateEntity.class, ExchangeRateDto.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(ExchangeRateEntity::getFromCurrency, ExchangeRateDto::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(ExchangeRateEntity::getToCurrency, ExchangeRateDto::setToCurrency);
                    mapper.map(ExchangeRateEntity::getScale, ExchangeRateDto::setScale);
                    mapper.map(ExchangeRateEntity::getRate, ExchangeRateDto::setExchangeRate);
                });

        modelMapper.typeMap(ExchangeRateDto.class, ExchangeRateEntity.class)
                .addMappings(mapper -> {
                    mapper.using(currencyCodeEnumStringConverter).map(ExchangeRateDto::getFromCurrency, ExchangeRateEntity::setFromCurrency);
                    mapper.using(currencyCodeEnumStringConverter).map(ExchangeRateDto::getToCurrency, ExchangeRateEntity::setToCurrency);
                    mapper.map(ExchangeRateDto::getScale, ExchangeRateEntity::setScale);
                    mapper.map(ExchangeRateDto::getExchangeRate, ExchangeRateEntity::setRate);
                });
    }

    private ModelMapperConfig() {
    }

    public static ModelMapper getInstance() {
        return modelMapper;
    }

}
