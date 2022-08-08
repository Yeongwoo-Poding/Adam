package project.adam.service.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.Privilege;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class MemberJoinRequest {

    @NotEmpty
    private String id;

    @NotEmpty
    private String name;

    private Privilege privilege = Privilege.USER;

    public MemberJoinRequest(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public MemberJoinRequest(String id, String name, Privilege privilege) {
        this.id = id;
        this.name = name;
        this.privilege = privilege;
    }
}
