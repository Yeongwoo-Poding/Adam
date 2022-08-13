package project.adam.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.validator.UUIDPattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {

    @UUIDPattern
    private String id;
}
