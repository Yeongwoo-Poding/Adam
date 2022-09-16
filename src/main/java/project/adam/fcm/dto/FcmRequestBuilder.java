package project.adam.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import project.adam.entity.member.Member;

@Getter
@Builder
@AllArgsConstructor
public class FcmRequestBuilder {
    private Member member;
    private String title;
    private String body;
    private Long postId;
}
