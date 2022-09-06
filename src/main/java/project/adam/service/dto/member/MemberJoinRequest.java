package project.adam.service.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.member.Privilege;
import project.adam.validator.UUIDPattern;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class MemberJoinRequest {

    @UUIDPattern
    private String id;

    @NotEmpty
    private String email;

    @NotEmpty
    private String name;

    private Privilege privilege = Privilege.USER;

    public MemberJoinRequest(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public MemberJoinRequest(String id, String email, String name, Privilege privilege) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.privilege = privilege;
    }
}
