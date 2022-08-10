package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;

@Getter
public class MemberLoginResponse {
    String sessionId;

    public MemberLoginResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public MemberLoginResponse(Member member) {
        this.sessionId = member.getSessionId();
    }
}
