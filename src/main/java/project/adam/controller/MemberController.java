package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.member.MemberImageResponse;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.MemberService;
import project.adam.controller.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberLoginResponse joinMember(@Validated @RequestBody MemberJoinRequest memberDto) {
        UUID sessionId = memberService.join(memberDto);
        return new MemberLoginResponse(sessionId);
    }

    @PostMapping("/session")
    public MemberLoginResponse loginMember(@Validated @RequestBody MemberLoginRequest memberDto) {
        UUID sessionId = memberService.login(UUID.fromString(memberDto.getId()));
        return new MemberLoginResponse(sessionId);
    }

    @GetMapping
    public MemberFindResponse findMember(@RequestHeader UUID sessionId) {
        Member findMember = memberService.findBySessionId(sessionId);
        return new MemberFindResponse(findMember);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteMember(@RequestHeader UUID sessionId) {
        memberService.withdraw(sessionId);
    }

    @PostMapping("/image")
    @ResponseStatus(NO_CONTENT)
    public void saveImage(@RequestHeader UUID sessionId, @RequestPart MultipartFile image) throws IOException {
        Member findMember = memberService.findBySessionId(sessionId);

        if (image == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }

        memberService.updateImage(findMember, image);
    }

    @GetMapping("/image")
    public MemberImageResponse getImagePath(@RequestHeader UUID sessionId) {
        Member findMember = memberService.findBySessionId(sessionId);
        return new MemberImageResponse(memberService.hasImage(findMember), memberService.getImagePath(findMember));
    }

    @DeleteMapping("/image")
    public void removeImage(@RequestHeader UUID sessionId) {
        memberService.removeImage(memberService.findBySessionId(sessionId));
    }
}
