package org.example.repository;

import org.example.dto.ExchangeRate;
import org.example.repository.entity.ExchangeRateEntity;

import java.util.List;

public interface ExchangeRateRepository extends AutoCloseable {

    List<ExchangeRate> findAll();

    void insert(ExchangeRate exchangeRate);

    void delete(ExchangeRate exchangeRate);

}
