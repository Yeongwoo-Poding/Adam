package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.member.Member;
import project.adam.entity.member.Privilege;

import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberFindResponse {

    private UUID id;
    private String name;
    private String imageName;
    private Privilege privilege;

    public MemberFindResponse(Member member) {
        this.id = null;
        this.name = member.getName();
        this.imageName = member.getImageName();
        this.privilege = member.getPrivilege();
    }

    public MemberFindResponse(UUID id, Member member) {
        this.id = id;
        this.name = member.getName();
        this.imageName = member.getImageName();
        this.privilege = member.getPrivilege();
    }
}
