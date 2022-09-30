package project.adam.utils.push;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.adam.service.MemberService;
import project.adam.utils.push.dto.PushRequest;

@RestController
@RequiredArgsConstructor
public class PushController {

    private final PushUtils pushUtils;
    private final MemberService memberService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/message")
    public void pushAll(@RequestBody PushRequest request) {
        pushUtils.pushAll(request);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/members/{email}/message")
    public void pushTo(@PathVariable String email,  @RequestBody PushRequest request)  {
        pushUtils.pushTo(memberService.findByEmail(email), request);
    }
}
