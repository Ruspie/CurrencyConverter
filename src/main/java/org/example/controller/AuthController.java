package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.auth.AuthResponseDto;
import org.example.dto.auth.LoginRequestDto;
import org.example.dto.auth.LogoutRequestDto;
import org.example.dto.auth.RefreshTokenRequestDto;
import org.example.repository.UserRepository;
import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            List<String> roles = user.getRoles().stream().toList();

            String accessToken = jwtService.generateAccessToken(loginRequest.getUsername(), roles);
            String refreshToken = jwtService.generateRefreshToken(loginRequest.getUsername());

            refreshTokenService.createRefreshToken(user, refreshToken);

            return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неверные учетные данные"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDto refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Невалидный refresh-токен"));
        }

        RefreshTokenEntity storedToken = refreshTokenService.findByToken(refreshToken)
                .orElse(null);

        if (storedToken == null || storedToken.isRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh-токен отозван или не найден"));
        }

        if (storedToken.getExpiryDate().isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh-токен истёк"));
        }

        String username = jwtService.getUsernameFromRefreshToken(refreshToken);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream().toList();

        refreshTokenService.revokeToken(storedToken);

        String newAccessToken = jwtService.generateAccessToken(username, roles);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        refreshTokenService.createRefreshToken(user, newRefreshToken);

        return ResponseEntity.ok(new AuthResponseDto(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequest) {
        refreshTokenService.findByToken(logoutRequest.getRefreshToken())
                .ifPresent(refreshTokenService::revokeToken);

        return ResponseEntity.ok(Map.of("message", "Вы успешно вышли"));
    }

}