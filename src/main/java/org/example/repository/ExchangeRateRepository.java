package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;

import java.util.List;

public interface ExchangeRateRepository extends AutoCloseable {

    List<ExchangeRateEntity> findAll();

    void insert(ExchangeRateEntity exchangeRate);

    void delete(ExchangeRateEntity exchangeRate);

}
