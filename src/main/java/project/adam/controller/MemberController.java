package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtil;
import project.adam.security.dto.RefreshTokenDto;
import project.adam.security.dto.TokenDto;
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
    public void joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        memberService.join(memberDto);
    }

    @PostMapping("/login")
    public TokenDto loginMember(@Validated @RequestBody MemberLoginRequest memberDto) {
        return memberService.login(memberDto);
    }

    @PostMapping("/refresh")
    public TokenDto refreshToken(@Validated @RequestBody RefreshTokenDto memberDto) {
        return memberService.refreshToken(memberDto);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String email) {
        return new MemberFindResponse(memberService.findByEmail(email));
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    public void deleteMember() {
        Member member = memberService.findByEmail(SecurityUtil.getCurrentMemberEmail());
        memberService.withdraw(member);
    }

    @Secured("ROLE_USER")
    @PostMapping("/image")
    public void saveImage(@RequestPart MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        memberService.saveImage(image);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/image")
    public void removeImage() {
        memberService.removeImage();
    }
}
