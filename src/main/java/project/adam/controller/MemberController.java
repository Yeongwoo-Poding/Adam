package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberFindResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        Long savedId = memberService.join(memberDto);
        return new MemberFindResponse(memberService.find(savedId));
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String id) {
        return new MemberFindResponse(memberService.find(id));
    }

    @DeleteMapping
    public void deleteMember(@RequestParam String id) {
        memberService.withdraw(id);
    }
}
