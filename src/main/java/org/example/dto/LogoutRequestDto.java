package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на выход")
public class LogoutRequestDto {

    @Schema(description = "Refresh JWT-токен для отзыва", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

}
