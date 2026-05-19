package org.example.config;

import org.example.dto.CurrencyCodeEnum;
import org.example.dto.ExchangeRate;
import org.example.dto.external.NBRBExchangeRate;
import org.example.repository.entity.ExchangeRateEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        //mapper.getConfiguration().set

        return mapper;
    }

}
