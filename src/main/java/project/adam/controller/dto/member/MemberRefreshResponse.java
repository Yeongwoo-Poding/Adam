package project.adam.controller.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRefreshResponse {
    private String accessToken;
    private String refreshToken;
}
