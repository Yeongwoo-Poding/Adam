package project.adam.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {

    @NotEmpty
    private String id;

    @NotEmpty
    private String email;

    @NotEmpty
    private String deviceToken;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, id);
    }
}
