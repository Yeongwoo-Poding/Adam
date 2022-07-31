package project.adam.service.dto.member;

import lombok.Getter;
import project.adam.entity.Member;

@Getter
public class MemberFindResponse {

    private String uuid;
    private String nickname;

    public MemberFindResponse(Member member) {
        this.uuid = member.getUuid();
        this.nickname = member.getNickname();
    }
}
