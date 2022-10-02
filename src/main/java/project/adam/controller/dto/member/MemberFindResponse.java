package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.member.Member;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberFindResponse {

    private String name;
    private String email;
    private String image;

    public MemberFindResponse(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.image = member.getImage();
    }
}
