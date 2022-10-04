package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.request.member.MemberJoinRequest;
import project.adam.controller.dto.request.member.MemberLoginRequest;
import project.adam.controller.dto.request.member.MemberUpdateControllerRequest;
import project.adam.controller.dto.response.member.MemberFindResponse;
import project.adam.controller.dto.response.member.MemberLoginResponse;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberUpdateServiceRequest;


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

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/logout")
    public void logoutMember() {
        memberService.logout(SecurityUtils.getCurrentMemberEmail());
    }

//    @PostMapping("/refresh")
//    public MemberLoginResponse refreshToken(@Validated @RequestBody MemberRefreshResponse memberDto) {
//        return memberService.refreshToken(memberDto);
//    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/me")
    public MemberFindResponse findMe() {
        return new MemberFindResponse(memberService.findByEmail(SecurityUtils.getCurrentMemberEmail()));
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestParam String email) {
        return new MemberFindResponse(memberService.findByEmail(email));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping
    public void updateMember(@RequestBody MemberUpdateControllerRequest request) {
        memberService.update(new MemberUpdateServiceRequest(SecurityUtils.getCurrentMemberEmail(), request));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PatchMapping("/notification/post")
    public void setPostNotification() {
        memberService.setPostPushNotification(SecurityUtils.getCurrentMemberEmail());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PatchMapping("/notification/comment")
    public void setCommentNotification() {
        memberService.setCommentPushNotification(SecurityUtils.getCurrentMemberEmail());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping
    public void deleteMember() {
        memberService.withdraw(SecurityUtils.getCurrentMemberEmail());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/image")
    public void saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        memberService.saveImage(SecurityUtils.getCurrentMemberEmail(), image);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/image")
    public void removeImage() {
        memberService.removeImage(SecurityUtils.getCurrentMemberEmail());
    }
}
