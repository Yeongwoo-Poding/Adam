package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.member.Member;

import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberFindResponse {

    private UUID id;
    private String name;
    private String email;
    private String image;

    public MemberFindResponse(Member member) {
        this.id = null;
        this.name = member.getName();
        this.email = member.getEmail();
        this.image = member.getImage();
    }
}
