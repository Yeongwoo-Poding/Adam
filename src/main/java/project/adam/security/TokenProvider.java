package project.adam.security;

import org.springframework.security.core.Authentication;
import project.adam.controller.dto.member.MemberLoginResponse;

public interface TokenProvider {
    MemberLoginResponse generateTokenDto(Authentication authentication);
    Authentication getAuthentication(String accessToken);
    boolean validateToken(String token);
}
