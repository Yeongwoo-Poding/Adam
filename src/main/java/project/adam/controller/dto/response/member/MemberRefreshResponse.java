package project.adam.controller.dto.response.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRefreshResponse {
    
    private String accessToken;
    private String refreshToken;
}
