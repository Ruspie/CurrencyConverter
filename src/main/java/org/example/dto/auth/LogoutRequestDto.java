package org.example.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {

    private String refreshToken;

}
