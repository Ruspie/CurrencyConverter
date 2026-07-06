package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long>, ExchangeRateRepositoryCustom {

    @Query("""
        SELECT e FROM ExchangeRateEntity e
        WHERE e.fromCurrency = :from
          AND e.toCurrency = :to
    """)
    Optional<ExchangeRateEntity> findByCurrencyPair(@Param("from") String from, @Param("to") String to);

    List<ExchangeRateEntity> findByFromCurrency(String fromCurrency);

}