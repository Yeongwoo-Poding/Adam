package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.entity.Member;
import project.adam.entity.Privilege;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static project.adam.entity.Privilege.*;
import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberLoginResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        String savedId = memberService.join(memberDto);

        log.info("Join Member {}", savedId);
        return loginMember(savedId);
    }

    @GetMapping("/{memberId}")
    public MemberFindResponse findMember(@RequestHeader("sessionId") String sessionId,
                                         @PathVariable String memberId) {
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), memberId) ? USER : ADMIN);

        log.info("Find Member {}", memberId);
        return new MemberFindResponse(memberService.find(memberId));
    }

    @PatchMapping("/{memberId}")
    public MemberLoginResponse loginMember(@PathVariable String memberId) {
        String sessionId = memberService.login(memberId);
        return new MemberLoginResponse(sessionId);
    }

    @DeleteMapping("/{memberId}")
    public void deleteMember(@RequestHeader("sessionId") String sessionId,
                             @PathVariable String memberId) {
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), memberId) ? USER : ADMIN);
        memberService.withdraw(memberId);

        log.info("Delete Member {}", memberId);
    }
}
