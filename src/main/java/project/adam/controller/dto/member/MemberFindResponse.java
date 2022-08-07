package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;
import project.adam.entity.Privilege;

@Getter
public class MemberFindResponse {

    private String id;
    private String nickname;
    private Privilege privilege;

    public MemberFindResponse(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.privilege = member.getPrivilege();
    }
}
