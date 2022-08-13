package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;

import java.util.UUID;

@Getter
public class MemberLoginResponse {
    UUID sessionId;

    public MemberLoginResponse(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public MemberLoginResponse(Member member) {
        this.sessionId = member.getSessionId();
    }
}
