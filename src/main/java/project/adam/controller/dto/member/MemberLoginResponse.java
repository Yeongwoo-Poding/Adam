package project.adam.controller.dto.member;

import lombok.Getter;
import project.adam.entity.Member;

import java.util.UUID;

@Getter
public class MemberLoginResponse {

    UUID token;

    public MemberLoginResponse(UUID token) {
        this.token = token;
    }

    public MemberLoginResponse(Member member) {
        this.token = member.getToken();
    }
}
