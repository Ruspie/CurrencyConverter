package org.example.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "exchange_rate", schema = "cur_ex")
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "from_currency", nullable = false)
    private String fromCurrency;
    @Column(name = "to_currency", nullable = false)
    private String toCurrency;
    @Column(nullable = false)
    private BigDecimal rate;
    @Column(nullable = false)
    private BigDecimal scale;
    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

}
