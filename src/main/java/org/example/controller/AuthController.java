package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.AuthResponseDto;
import org.example.dto.ErrorResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.dto.LogoutRequestDto;
import org.example.dto.MessageResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
@Tag(name = "Auth", description = "Аутентификация и управление JWT-токенами")
public class AuthController {

    private  final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и выдача access/refresh токенов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные username/password",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<?> login (@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
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
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Неверные username/password"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токенов", description = "Выдача новой пары access/refresh токенов по действующему refresh-токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Неверный, отозванный или истёкший refresh-токен",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
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

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken, user.getUsername(), userRoles));
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Отзыв refresh-токена")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный выход",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))
    })
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        refreshTokenRepository.findByToken(logoutRequestDto.getRefreshToken())
                .ifPresent(refreshTokenService::revokeToken);

        return ResponseEntity.ok(Map.of("message", "Вы успешно вышли"));
    }

}
