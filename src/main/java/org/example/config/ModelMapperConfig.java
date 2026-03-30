package org.example.config;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.external.NBRBExchangeRate;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class ModelMapperConfig {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        Converter<String, CurrencyCodeEnum> stringCurrencyCodeEnumConverter = mappingContext -> CurrencyCodeEnum.valueOf(mappingContext.getSource());

        modelMapper.typeMap(NBRBExchangeRate.class, ExchangeRate.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(nbrbExchangeRate -> "BYN", ExchangeRate::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(NBRBExchangeRate::getToCurrency, ExchangeRate::setToCurrency);
                    mapper.map(NBRBExchangeRate::getScale, ExchangeRate::setScale);
                    mapper.map(NBRBExchangeRate::getExchangeRate, ExchangeRate::setExchangeRate);
                });
    }

    private ModelMapperConfig() {
    }

    public static ModelMapper getInstance() {
        return modelMapper;
    }

}
