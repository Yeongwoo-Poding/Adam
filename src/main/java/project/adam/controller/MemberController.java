package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;

import java.io.IOException;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public void joinMember(@Validated @RequestBody MemberJoinRequest request) {
        memberService.join(request);
    }

    @PostMapping("/login")
    public MemberLoginResponse loginMember(@Validated @RequestBody MemberLoginRequest request) {
        return memberService.login(request);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/logout")
    public void logoutMember() {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        memberService.logout(member);
    }

//    @PostMapping("/refresh")
//    public MemberLoginResponse refreshToken(@Validated @RequestBody MemberRefreshResponse memberDto) {
//        return memberService.refreshToken(memberDto);
//    }

    @Secured("ROLE_USER")
    @GetMapping("/me")
    public MemberFindResponse findMe() {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        return new MemberFindResponse(member);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String email) {
        return new MemberFindResponse(memberService.findByEmail(email));
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    public void deleteMember() {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        memberService.withdraw(member);
    }

    @Secured("ROLE_USER")
    @PostMapping("/image")
    public void saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        memberService.saveImage(member, image);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/image")
    public void removeImage() {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        memberService.removeImage(member);
    }
}
