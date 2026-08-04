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
@Schema(description = "Информационное сообщение")
public class MessageResponseDto {

    @Schema(description = "Текст сообщения", example = "Вы успешно вышли")
    private String message;

}
