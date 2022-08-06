package project.adam.service.dto.member;

import lombok.Getter;
import project.adam.entity.Member;
import project.adam.entity.Privilege;

@Getter
public class MemberFindResponse {

    private String uuid;
    private String nickname;
    private Privilege privilege;

    public MemberFindResponse(Member member) {
        this.uuid = member.getUuid();
        this.nickname = member.getNickname();
        this.privilege = member.getPrivilege();
    }
}
