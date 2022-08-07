package project.adam.service.dto.member;

import lombok.AllArgsConstructor;
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
    private String nickname;

    private Privilege privilege = Privilege.USER;

    public MemberJoinRequest(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public MemberJoinRequest(String id, String nickname, Privilege privilege) {
        this.id = id;
        this.nickname = nickname;
        this.privilege = privilege;
    }
}
