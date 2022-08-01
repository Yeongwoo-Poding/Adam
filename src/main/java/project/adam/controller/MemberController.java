package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/new")
    public MemberFindResponse joinMember(@RequestBody MemberJoinRequest memberDto) {
        log.debug("memberDto.uuid: {}", memberDto.getId());
        log.debug("memberDto.nickname: {}", memberDto.getNickname());
        Long savedId = memberService.join(memberDto);
        log.info("member join uuid={}", memberDto.getId());
        return memberService.find(savedId);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String id) {
        MemberFindResponse memberFindResponse = memberService.find(id);
        log.debug("memberFindResponse.uuid: {}", memberFindResponse.getUuid());
        log.debug("memberFindResponse.nickname: {}", memberFindResponse.getNickname());
        log.info("member find uuid={}", memberFindResponse.getUuid());
        return memberFindResponse;
    }

    @DeleteMapping
    public void deleteMember(@RequestParam String id) {
        memberService.withdraw(id);
        log.info("member delete uuid={}", id);
    }
}
