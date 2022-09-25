package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberLoginResponse {

    private String accessToken;
    private String refreshToken;

    @Builder
    public MemberLoginResponse(String accessToken, String refreshToken, Date expiredDate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}