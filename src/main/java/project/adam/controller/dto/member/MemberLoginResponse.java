package project.adam.controller.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}