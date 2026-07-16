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
        revokeAllUserRefreshTokens(user.getId());

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(Long userId) {
        refreshTokenRepository.revokeAllTokensByUserId(userId);
    }

    @Override
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void revokeToken(RefreshTokenEntity refreshTokenEntity) {
        refreshTokenEntity.setRevoked(true);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public List<RefreshTokenEntity> findActiveRefreshTokensByUserId(Long userId) {
        return refreshTokenRepository.findAllByUserIdAndRevoked(userId, false);
    }

}
