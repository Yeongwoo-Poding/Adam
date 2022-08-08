package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;
import project.adam.entity.Privilege;

@Getter
public class MemberFindResponse {

    private String id;
    private String name;
    private Privilege privilege;

    public MemberFindResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.privilege = member.getPrivilege();
    }
}
