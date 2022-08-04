package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.exception.ApiException;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;

import static project.adam.exception.ExceptionEnum.INVALID_DATA;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/new")
    public MemberFindResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(INVALID_DATA);
        }

        Long savedId = memberService.join(memberDto);
        return memberService.find(savedId);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String id) {
        return memberService.find(id);
    }

    @DeleteMapping
    public void deleteMember(@RequestParam String id) {
        memberService.withdraw(id);
    }
}
