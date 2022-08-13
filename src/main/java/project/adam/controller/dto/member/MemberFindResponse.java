package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;
import project.adam.entity.Privilege;

import java.util.UUID;

@Getter
public class MemberFindResponse {

    private UUID id;
    private String name;
    private Privilege privilege;

    public MemberFindResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.privilege = member.getPrivilege();
    }
}
