package project.adam.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.exception.ApiException;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static project.adam.exception.ExceptionEnum.AUTHENTICATION_FAILED;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public MemberLoginResponse generateTokenDto(Authentication authentication) {
        String authorites = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date accessExpireDate = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshExpireDate = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorites)
                .setExpiration(accessExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessExpireDate.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new ApiException(AUTHENTICATION_FAILED);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
