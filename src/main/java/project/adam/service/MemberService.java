package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.request.member.MemberJoinRequest;
import project.adam.controller.dto.request.member.MemberLoginRequest;
import project.adam.controller.dto.response.member.MemberLoginResponse;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.repository.member.MemberRepository;
import project.adam.security.TokenProvider;
import project.adam.service.dto.member.MemberUpdateServiceRequest;
import project.adam.utils.image.ImageUtils;

import java.util.List;

import static project.adam.exception.ExceptionEnum.DUPLICATED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ImageUtils imageUtils;


    @Transactional
    public Member join(MemberJoinRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(DUPLICATED);
        }

        Member createdMember = Member.builder()
                .id(passwordEncoder.encode(request.getId()))
                .email(request.getEmail())
                .name(request.getName())
                .session(request.getSession())
                .build();

        return memberRepository.save(createdMember);
    }

    @Transactional
    public MemberLoginResponse login(MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenResponse(authenticate);
//        RefreshToken refreshToken = RefreshToken.builder()
//                .key(authenticate.getName())
//                .value(memberLoginResponse.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);

        Member loginMember = memberRepository.findByEmail(authenticate.getName()).orElseThrow();
        loginMember.login(request.getDeviceToken());

        return memberLoginResponse;
    }

    @Transactional
    public void logout(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.logout();
    }

//    @Transactional
//    public MemberLoginResponse refreshToken(MemberRefreshResponse memberDto) {
//        if (!tokenProvider.validateToken(memberDto.getRefreshToken())) {
//            throw new ApiException(AUTHENTICATION_FAILED);
//        }
//
//        Authentication authentication = tokenProvider.getAuthentication(memberDto.getAccessToken());
//        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName()).orElseThrow(() -> new ApiException(AUTHENTICATION_FAILED));
//
//        if (!refreshToken.getValue().equals(memberDto.getRefreshToken())) {
//            throw new ApiException(AUTHENTICATION_FAILED);
//        }
//
//        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenDto(authentication);
//
//        RefreshToken newRefreshToken = refreshToken.updateValue(memberLoginResponse.getRefreshToken());
//        refreshTokenRepository.save(newRefreshToken);
//
//        return memberLoginResponse;
//    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }

    public List<Member> findLoginUsers() {
        return memberRepository.findLoginUsers();
    }

    @Transactional
    public void update(MemberUpdateServiceRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        member.update(request.getName(), request.getSession());
    }

    @Transactional
    public void setPostPushNotification(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.togglePostNotification();
    }

    @Transactional
    public void setCommentPushNotification(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.toggleCommentNotification();
    }

    @Transactional
    public void withdraw(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        memberRepository.remove(member);
    }

    @Transactional
    public void saveImage(String email, MultipartFile image) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        imageUtils.removeImageFile(member.getImage());

        String imageName = imageUtils.createImageName(image);
        imageUtils.createImageFile(imageName, image);
        member.setImage(imageName);
    }

    @Transactional
    public void removeImage(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        imageUtils.removeImageFile(member.getImage());
        member.setImage(null);
    }
}
