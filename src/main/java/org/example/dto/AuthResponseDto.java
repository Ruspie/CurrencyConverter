package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с JWT-токенами")
public class AuthResponseDto {

    @Schema(description = "Access JWT-токен")
    private String accessToken;

    @Schema(description = "Refresh JWT-токен")
    private String refreshToken;

    @Schema(description = "Имя пользователя", example = "admin")
    private String username;

    @Schema(description = "Роли пользователя", example = "[\"ADMIN\"]")
    private List<String> roles;

}
