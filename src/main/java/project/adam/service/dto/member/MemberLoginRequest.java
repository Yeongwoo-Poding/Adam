package project.adam.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import project.adam.validator.UUIDPattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {

    @UUIDPattern
    private String id;

    private String email;

    private String deviceToken;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, id);
    }
}
