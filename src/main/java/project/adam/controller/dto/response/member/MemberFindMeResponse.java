package project.adam.controller.dto.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberSession;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberFindMeResponse {

    private final String name;
    private final String email;
    private final MemberSession session;
    private final String image;
    private final boolean allowPostNotification;
    private final boolean allowCommentNotification;

    public MemberFindMeResponse(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.session = member.getSession();
        this.image = member.getImage();
        this.allowPostNotification = member.isAllowPostNotification();
        this.allowCommentNotification = member.isAllowCommentNotification();
    }
}
