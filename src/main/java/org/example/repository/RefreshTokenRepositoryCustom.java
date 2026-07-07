package org.example.repository;

import org.example.repository.entity.RefreshTokenEntity;

import java.util.List;

public interface RefreshTokenRepositoryCustom {

    List<RefreshTokenEntity> findActiveTokensByUserId(Long userId);

}