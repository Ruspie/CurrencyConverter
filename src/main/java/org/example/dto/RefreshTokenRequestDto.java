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
@Schema(description = "Запрос на обновление токенов")
public class RefreshTokenRequestDto {

    @Schema(description = "Refresh JWT-токен", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

}
