package org.example.config;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.external.NBRBExchangeRate;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

public class ModelMapperConfig {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        Converter<String, CurrencyCodeEnum> stringCurrencyCodeEnumConverter = mappingContext -> CurrencyCodeEnum.valueOf(mappingContext.getSource());
        Converter<CurrencyCodeEnum, String> currencyCodeEnumStringConverter = mappingContext -> mappingContext.getSource().name();

        modelMapper.typeMap(NBRBExchangeRate.class, ExchangeRate.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(nbrbExchangeRate -> "BYN", ExchangeRate::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(NBRBExchangeRate::getToCurrency, ExchangeRate::setToCurrency);
                    mapper.map(NBRBExchangeRate::getScale, ExchangeRate::setScale);
                    mapper.map(NBRBExchangeRate::getExchangeRate, ExchangeRate::setExchangeRate);
                });

        modelMapper.typeMap(ExchangeRateEntity.class, ExchangeRate.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(ExchangeRateEntity::getFromCurrency, ExchangeRate::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(ExchangeRateEntity::getToCurrency, ExchangeRate::setToCurrency);
                    mapper.map(ExchangeRateEntity::getScale, ExchangeRate::setScale);
                    mapper.map(ExchangeRateEntity::getRate, ExchangeRate::setExchangeRate);
                });

        modelMapper.typeMap(ExchangeRate.class, ExchangeRateEntity.class)
                .addMappings(mapper -> {
                    mapper.using(currencyCodeEnumStringConverter).map(ExchangeRate::getFromCurrency, ExchangeRateEntity::setFromCurrency);
                    mapper.using(currencyCodeEnumStringConverter).map(ExchangeRate::getToCurrency, ExchangeRateEntity::setToCurrency);
                    mapper.map(ExchangeRate::getScale, ExchangeRateEntity::setScale);
                    mapper.map(ExchangeRate::getExchangeRate, ExchangeRateEntity::setRate);
                });
    }

    private ModelMapperConfig() {
    }

    public static ModelMapper getInstance() {
        return modelMapper;
    }

}
