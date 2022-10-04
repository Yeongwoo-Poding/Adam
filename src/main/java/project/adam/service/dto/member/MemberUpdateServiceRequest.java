package project.adam.service.dto.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.member.MemberUpdateControllerRequest;
import project.adam.entity.member.MemberSession;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateServiceRequest {

    private String email;
    private String name;
    private MemberSession session;

    public MemberUpdateServiceRequest(String email, MemberUpdateControllerRequest request) {
        this.email = email;
        this.name = request.getName();
        this.session = request.getSession();
    }
}
