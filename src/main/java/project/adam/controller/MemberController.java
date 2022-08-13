package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.entity.Member;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;

import java.util.Objects;
import java.util.UUID;

import static project.adam.entity.Privilege.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberLoginResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        UUID sessionId = memberService.join(memberDto);

        log.info("Join Member sessionId={}", sessionId);
        return new MemberLoginResponse(sessionId);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestHeader UUID sessionId) {
        Member findMember = memberService.findBySessionId(sessionId);

        log.info("Find Member {}", findMember);
        return new MemberFindResponse(findMember);
    }

    @PatchMapping
    public MemberLoginResponse loginMember(@Validated @RequestBody MemberLoginRequest memberDto) {
        UUID sessionId = memberService.login(UUID.fromString(memberDto.getId()));

        log.info("Login Member sessionId={}", sessionId);
        return new MemberLoginResponse(sessionId);
    }

    @DeleteMapping
    public void deleteMember(@RequestHeader UUID sessionId) {
        memberService.withdraw(sessionId);

        log.info("Delete Member {}", sessionId);
    }
}
