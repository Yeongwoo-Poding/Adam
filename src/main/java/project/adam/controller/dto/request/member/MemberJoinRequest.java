package project.adam.controller.dto.request.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.member.Authority;
import project.adam.entity.member.MemberSession;
import project.adam.validator.UUIDPattern;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJoinRequest {

    @NotEmpty
    @UUIDPattern
    private String id;

    @NotEmpty
    private String email;

    @NotEmpty
    private String name;

    @NotNull
    private MemberSession session;

    private Authority authority = Authority.ROLE_USER;

    public MemberJoinRequest(String id, String email, String name, MemberSession session) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.session = session;
    }

    public MemberJoinRequest(String id, String email, String name, MemberSession session, Authority authority) {
        this(id, email, name, session);
        this.authority = authority;
    }
}
