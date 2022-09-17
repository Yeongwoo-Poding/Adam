package project.adam.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.adam.entity.member.Member;
import project.adam.fcm.FcmService;
import project.adam.fcm.dto.FcmRequestBuilder;
import project.adam.fcm.dto.FcmTestRequest;
import project.adam.service.MemberService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;
    private final MemberService memberService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/message")
    public FcmTestResponse testMessage(@RequestBody FcmTestRequest request) throws IOException {
        List<Member> members = memberService.findAll().stream().filter(Member::isLogin).collect(Collectors.toList());
        for (Member member : members) {
            FcmRequestBuilder fcmRequest = FcmRequestBuilder.builder()
                    .member(member)
                    .title(request.getTitle())
                    .body(request.getBody())
                    .postId(request.getPostId())
                    .build();
            fcmService.sendMessageTo(fcmRequest);
        }
        return new FcmTestResponse(members.size(), members.stream().map(Member::getEmail).collect(Collectors.toList()));
    }

    @Getter
    @AllArgsConstructor
    static class FcmTestResponse {
        private int count;
        private List<String> emails = new ArrayList<>();
    }
}
