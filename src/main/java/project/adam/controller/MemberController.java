package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.controller.dto.member.MemberImageResponse;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberLoginResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        UUID token = memberService.join(memberDto);
        return new MemberLoginResponse(token);
    }

    @PostMapping("/session")
    public MemberLoginResponse loginMember(@Validated @RequestBody MemberLoginRequest memberDto) {
        UUID token = memberService.login(UUID.fromString(memberDto.getId()));
        return new MemberLoginResponse(token);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestHeader UUID token) {
        Member findMember = memberService.findByToken(token);
        return new MemberFindResponse(findMember);
    }

    @DeleteMapping
    public void deleteMember(@RequestHeader UUID token) {
        memberService.withdraw(token);
    }

    @PostMapping("/image")
    public void saveImage(@RequestHeader UUID token, @RequestPart MultipartFile image) throws IOException {
        Member findMember = memberService.findByToken(token);

        if (image.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }

        memberService.saveImage(findMember, image);
    }

    @GetMapping("/image")
    public MemberImageResponse getImageName(@RequestHeader UUID token) {
        Member findMember = memberService.findByToken(token);
        return new MemberImageResponse(memberService.hasImage(findMember), memberService.getImageName(findMember));
    }

    @DeleteMapping("/image")
    public void removeImage(@RequestHeader UUID token) {
        memberService.removeImage(memberService.findByToken(token));
    }
}
