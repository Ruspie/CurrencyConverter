package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findFirstByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

    List<ExchangeRateRepository> findByFromCurrency(String fromCurrency);

}
