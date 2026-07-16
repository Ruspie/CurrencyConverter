package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findFirstByFromCurrencyAndToCurrencyAndRateDate(String fromCurrency, String toCurrency, LocalDate rateDate);

    List<ExchangeRateRepository> findByFromCurrency(String fromCurrency);

    List<ExchangeRateEntity> findAllByRateDate(LocalDate rateDate);

    List<ExchangeRateEntity> findAllByRateDateOrderByFromCurrencyAscToCurrencyAsc(LocalDate rateDate);

    @Query("SELECT DISTINCT r.rateDate FROM ExchangeRateEntity r ORDER BY r.rateDate DESC")
    List<LocalDate> findDistinctRateDates();
}
