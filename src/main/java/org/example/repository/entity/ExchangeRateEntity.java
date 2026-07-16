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
@Table(
        name = "exchange_rate",
        schema = "cur_ex",
        uniqueConstraints = @UniqueConstraint(
                name = "exchange_rate_currency_pair_date_unique",
                columnNames = {"from_currency", "to_currency", "rate_date"}
        )
)
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;
    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;
    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal rate;
    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal scale;
    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

}
