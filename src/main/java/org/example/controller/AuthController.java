package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuthResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.dto.LogoutRequestDto;
import org.example.dto.RefreshTokenRequestDto;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + loginRequestDto.getUsername() + " не найден"));

        List<String> userRoles = user.getRoles().stream().toList();

        String accessToken = jwtService.generateAccessToken(loginRequestDto.getUsername(), userRoles);
        String refreshToken = jwtService.generateRefreshToken(loginRequestDto.getUsername());

        refreshTokenService.createRefreshToken(user, refreshToken);

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken, user.getUsername(), userRoles));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        if (!jwtService.validateRefreshToken(refreshTokenRequestDto.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Неверный refresh-токен"));
        }

        RefreshTokenEntity storedRefreshToken = refreshTokenRepository.findByToken(refreshTokenRequestDto.getRefreshToken())
                .orElse(null);

        if (storedRefreshToken == null || storedRefreshToken.getRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh-токен не найден/отозван"));
        }

        if (storedRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh-токен истёк"));
        }

        String username = jwtService.getUsernameFromRefreshToken(refreshTokenRequestDto.getRefreshToken());
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " не найден"));

        refreshTokenService.revokeToken(storedRefreshToken);

        List<String> userRoles = user.getRoles().stream().toList();

        String accessToken = jwtService.generateAccessToken(username, userRoles);
        String refreshToken = jwtService.generateRefreshToken(username);

        refreshTokenService.createRefreshToken(user, refreshToken);

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken, username, userRoles));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        refreshTokenRepository.findByToken(logoutRequestDto.getRefreshToken())
                .ifPresent(refreshTokenService::revokeToken);

        return ResponseEntity.ok(Map.of("message", "Вы успешно вышли"));
    }
}
