package project.adam.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenDto {
    private String accessToken;
    private String refreshToken;
}
