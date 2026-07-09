package org.example.service;

import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshTokenEntity createRefreshToken(UserEntity user, String token);

    void revokeAllUserRefreshTokens(Long userId);

    Optional<RefreshTokenEntity> findByToken(String token);

    void revokeToken(RefreshTokenEntity refreshTokenEntity);

    List<RefreshTokenEntity> findActiveRefreshTokensByUserId(Long userId);

}
