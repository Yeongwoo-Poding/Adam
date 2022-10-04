package project.adam.admin.dto;

import lombok.Getter;
import project.adam.entity.member.Member;
import project.adam.utils.DateUtils;

@Getter
public class BanResponse {

    private String email;
    private String suspendedDate;

    public BanResponse(Member member) {
        this.email = member.getEmail();
        this.suspendedDate = DateUtils.getFormattedDate(member.getSuspendedDate());
    }
}
