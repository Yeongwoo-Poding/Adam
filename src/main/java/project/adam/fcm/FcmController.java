package project.adam.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.adam.fcm.dto.FcmPushRequest;
import project.adam.service.MemberService;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;
    private final MemberService memberService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/message")
    public void pushAll(@RequestBody FcmPushRequest request) {
        fcmService.pushAll(request);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/members/{email}/message")
    public void pushTo(@PathVariable String email,  @RequestBody FcmPushRequest request)  {
        fcmService.pushTo(memberService.findByEmail(email), request);
    }
}
