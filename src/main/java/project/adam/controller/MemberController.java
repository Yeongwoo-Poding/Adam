package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.entity.Member;
import project.adam.entity.Privilege;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static project.adam.entity.Privilege.*;
import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberFindResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        String savedId = memberService.join(memberDto);
        return new MemberFindResponse(memberService.find(savedId));
    }

    @GetMapping
    public MemberFindResponse findMember(@CookieValue("sessionId") String sessionId,
                                         @RequestParam String id) {
        memberService.find(sessionId).authorization(sessionId.equals(id) ? USER : ADMIN);
        return new MemberFindResponse(memberService.find(id));
    }

    @DeleteMapping
    public void deleteMember(@CookieValue("sessionId") String sessionId,
                             @RequestParam String id) {
        memberService.find(sessionId).authorization(sessionId.equals(id) ? USER : ADMIN);
        memberService.withdraw(id);
    }
}
