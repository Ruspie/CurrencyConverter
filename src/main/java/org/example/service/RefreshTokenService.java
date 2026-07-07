package org.example.service;

import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenService {

    public RefreshTokenEntity createRefreshToken(UserEntity user, String token);

    public void revokeAllUserTokens(Long userId);

    public Optional<RefreshTokenEntity> findByToken(String token);

    public void revokeToken(RefreshTokenEntity token);

    public List<RefreshTokenEntity> findActiveTokensByUserId(Long userId);

}
