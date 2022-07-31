package project.adam.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class MemberJoinRequest {

    @NotEmpty
    @UniqueElements
    private String uuid;

    @NotEmpty
    private String nickname;
}
