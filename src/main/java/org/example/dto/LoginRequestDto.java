package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на вход")
public class LoginRequestDto {

    @Schema(description = "Имя пользователя", example = "admin")
    private String username;

    @Schema(description = "Пароль", example = "password")
    private String password;

}
