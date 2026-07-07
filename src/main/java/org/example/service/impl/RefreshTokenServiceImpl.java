package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtProperties;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;
import org.example.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user, String token) {
        revokeAllUserTokens(user.getId());

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(entity);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void revokeToken(RefreshTokenEntity token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshTokenEntity> findActiveTokensByUserId(Long userId) {
        return refreshTokenRepository.findActiveTokensByUserId(userId);
    }

}