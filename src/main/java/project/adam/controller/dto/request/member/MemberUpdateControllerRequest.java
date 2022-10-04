package project.adam.controller.dto.request.member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.member.MemberSession;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberUpdateControllerRequest {

    @NotEmpty
    private String name;

    @NotNull
    private MemberSession session;
}
