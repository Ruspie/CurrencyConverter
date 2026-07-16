package org.example.repository.entity.mapper;

import org.example.dto.enums.CurrencyCodeEnum;
import org.example.dto.external.NBRBExchangeRateDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateMapperTest {

    private final ExchangeRateMapper mapper = createMapper();

    @Test
    void mapsNBRBDecimalValuesWithoutDoublePrecisionLoss() {
        NBRBExchangeRateDto source = new NBRBExchangeRateDto();
        source.setFromCurrency("RUB");
        source.setScale(new BigDecimal("100"));
        source.setExchangeRate(new BigDecimal("3.5123"));
        source.setDate("2026-07-15T00:00:00");

        var result = mapper.fromNBRB(source);

        assertThat(result.getFromCurrency()).isEqualTo(CurrencyCodeEnum.RUB);
        assertThat(result.getToCurrency()).isEqualTo(CurrencyCodeEnum.BYN);
        assertThat(result.getScale()).isEqualByComparingTo("100");
        assertThat(result.getExchangeRate()).isEqualByComparingTo("3.5123");
        assertThat(result.getRateDate()).isEqualTo(LocalDate.of(2026, 7, 15));
    }

    private ExchangeRateMapper createMapper() {
        ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper(new ModelMapper());
        exchangeRateMapper.setupMapperConfig();
        return exchangeRateMapper;
    }
}
