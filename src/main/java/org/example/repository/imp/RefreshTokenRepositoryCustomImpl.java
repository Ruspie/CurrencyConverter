package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.example.repository.entity.RefreshTokenEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class RefreshTokenRepositoryCustomImpl implements RefreshTokenRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RefreshTokenEntity> findActiveTokensByUserId(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RefreshTokenEntity> query = cb.createQuery(RefreshTokenEntity.class);
        Root<RefreshTokenEntity> root = query.from(RefreshTokenEntity.class);

        Predicate userIdPred = cb.equal(root.get("user").get("id"), userId);
        Predicate revokedPred = cb.isFalse(root.get("revoked"));
        Predicate expiryPred = cb.greaterThan(root.get("expiryDate"), Instant.now());

        query.where(cb.and(userIdPred, revokedPred, expiryPred));
        return entityManager.createQuery(query).getResultList();
    }

}