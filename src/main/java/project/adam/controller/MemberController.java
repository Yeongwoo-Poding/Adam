package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.service.dto.member.MemberLoginResponse;
import project.adam.entity.Member;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

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

    @GetMapping("/{memberId}")
    public MemberFindResponse findMember(@RequestHeader("sessionId") UUID sessionId,
                                         @PathVariable UUID memberId) {
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), memberId) ? USER : ADMIN);

        log.info("Find Member {}", memberId);
        return new MemberFindResponse(memberService.find(memberId));
    }

    @PatchMapping("/{memberId}")
    public MemberLoginResponse loginMember(@PathVariable UUID memberId) {
        UUID sessionId = memberService.login(memberId);
        return new MemberLoginResponse(sessionId);
    }

    @DeleteMapping("/{memberId}")
    public void deleteMember(@RequestHeader("sessionId") UUID sessionId,
                             @PathVariable UUID memberId) {
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), memberId) ? USER : ADMIN);
        memberService.withdraw(memberId);

        log.info("Delete Member {}", memberId);
    }
}
