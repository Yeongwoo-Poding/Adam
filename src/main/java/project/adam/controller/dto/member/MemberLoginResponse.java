package project.adam.controller.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

@Getter
@NoArgsConstructor
public class MemberLoginResponse {

    private String accessToken;
    private String refreshToken;
    private ZonedDateTime expiredAt;

    @Builder
    public MemberLoginResponse(String accessToken, String refreshToken, Date expiredDate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiredAt = ZonedDateTime.ofInstant(expiredDate.toInstant(), TimeZone.getDefault().toZoneId());
    }
}