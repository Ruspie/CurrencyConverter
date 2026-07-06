package org.example.repository;

import org.example.repository.entity.ExchangeRateEntity;

import java.util.List;

public interface ExchangeRateRepositoryCustom {

    List<ExchangeRateEntity> findWithFilters(String fromCurrency, String toCurrency, Boolean hasScaleGreaterThanOne);

}