package org.example.repository.entity.mapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.ExchangeRateDto;
import org.example.dto.external.NBRBExchangeRateDto;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeRateMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapperConfig() {
        Converter<String, CurrencyCodeEnum> stringCurrencyCodeEnumConverter = mappingContext -> CurrencyCodeEnum.valueOf(mappingContext.getSource());
        Converter<CurrencyCodeEnum, String> currencyCodeEnumStringConverter = mappingContext -> mappingContext.getSource().name();

        modelMapper.typeMap(NBRBExchangeRateDto.class, ExchangeRateDto.class)
                .addMappings(mapper -> {
                    mapper.using(stringCurrencyCodeEnumConverter).map(nbrbExchangeRateDto -> "BYN", ExchangeRateDto::setFromCurrency);
                    mapper.using(stringCurrencyCodeEnumConverter).map(NBRBExchangeRateDto::getToCurrency, ExchangeRateDto::setToCurrency);
                    mapper.map(NBRBExchangeRateDto::getScale, ExchangeRateDto::setScale);
                    mapper.map(NBRBExchangeRateDto::getExchangeRate, ExchangeRateDto::setExchangeRate);
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

}
