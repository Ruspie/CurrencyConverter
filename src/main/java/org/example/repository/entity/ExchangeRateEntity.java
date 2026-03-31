package org.example.repository.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRateEntity {

    private Long id;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    private BigDecimal scale;

}
