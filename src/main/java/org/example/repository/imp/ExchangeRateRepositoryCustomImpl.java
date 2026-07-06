package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.example.repository.ExchangeRateRepositoryCustom;
import org.example.repository.entity.ExchangeRateEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExchangeRateRepositoryCustomImpl implements ExchangeRateRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ExchangeRateEntity> findWithFilters(String fromCurrency, String toCurrency, Boolean hasScaleGreaterThanOne) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExchangeRateEntity> query = cb.createQuery(ExchangeRateEntity.class);
        Root<ExchangeRateEntity> root = query.from(ExchangeRateEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (fromCurrency != null && !fromCurrency.isBlank()) {
            predicates.add(cb.equal(root.get("fromCurrency"), fromCurrency));
        }
        if (toCurrency != null && !toCurrency.isBlank()) {
            predicates.add(cb.equal(root.get("toCurrency"), toCurrency));
        }
        if (hasScaleGreaterThanOne != null && hasScaleGreaterThanOne) {
            predicates.add(cb.greaterThan(root.get("scale"), BigDecimal.ONE));
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

}