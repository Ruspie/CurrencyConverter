package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findByFromCurrencyAndToCurrencyAndRateDate(
            String fromCurrency,
            String toCurrency,
            LocalDate rateDate
    );

    List<ExchangeRateEntity> findAllByRateDateIn(Set<LocalDate> rateDates);

    List<ExchangeRateEntity> findAllByRateDateOrderByFromCurrencyAscToCurrencyAsc(LocalDate rateDate);

    @Query("SELECT DISTINCT rate.rateDate FROM ExchangeRateEntity rate ORDER BY rate.rateDate DESC")
    List<LocalDate> findDistinctRateDates();
}
