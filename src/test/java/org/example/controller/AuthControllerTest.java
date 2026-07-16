package org.example.controller;

import org.example.dto.AuthResponseDto;
import org.example.dto.RefreshTokenRequestDto;
import org.example.repository.UserRepository;
import org.example.repository.entity.RefreshTokenEntity;
import org.example.repository.entity.UserEntity;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(
                authenticationManager,
                userRepository,
                jwtService,
                refreshTokenService
        );
    }

    @Test
    void rotatesAndPersistsRefreshTokenInResponseBody() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("user")
                .enabled(true)
                .roles(Set.of("ROLE_USER"))
                .build();
        RefreshTokenEntity stored = RefreshTokenEntity.builder()
                .token("old-refresh")
                .user(user)
                .revoked(false)
                .expiryDate(Instant.now().plusSeconds(60))
                .build();

        when(jwtService.validateRefreshToken("old-refresh")).thenReturn(true);
        when(refreshTokenService.findByToken("old-refresh")).thenReturn(Optional.of(stored));
        when(jwtService.getUsernameFromRefreshToken("old-refresh")).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken("user", java.util.List.of("ROLE_USER"))).thenReturn("access");
        when(jwtService.generateRefreshToken("user")).thenReturn("new-refresh");

        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("old-refresh");
        var response = controller.refresh(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOfSatisfying(AuthResponseDto.class, body -> {
            assertThat(body.getAccessToken()).isEqualTo("access");
            assertThat(body.getRefreshToken()).isEqualTo("new-refresh");
        });
        verify(refreshTokenService).createRefreshToken(user, "new-refresh");
    }
}
